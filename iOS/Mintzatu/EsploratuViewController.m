//
//  EsploratuViewController.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 10/09/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "EsploratuViewController.h"

#import <MapKit/MapKit.h>
#import "MintzatuAPIClient.h"
#import "MintzatuAnnotation.h"
#import "Place.h"
#import "PlaceCategory.h"
#import "LekuaInfoViewController.h"
#import "MKAnnotationView+WebCache.h"
#import "ZAActivityBar.h"

#define DISTANCE_MARGIN 10 * 1000

@interface EsploratuViewController () <MKMapViewDelegate>
{
    CLLocationCoordinate2D _userCoordinate;
    NSMutableArray *_places;
    UIBarButtonItem * _activityIndicatorBarButton;
    UIActivityIndicatorView *_activityIndicator;
    
    CLLocationManager *locationManager;
    BOOL fromError;
}
@property (weak, nonatomic) IBOutlet MKMapView *mapView;
@end

@implementation EsploratuViewController

- (void)dealloc
{
    _places = nil;
    _activityIndicatorBarButton = nil;
    _activityIndicator = nil;
    self.mapView = nil;
}

- (id)initWithCoder:(NSCoder *)aDecoder
{
    self = [super initWithCoder:aDecoder];
    if (self) {
        _places = [[NSMutableArray alloc] init];
        _activityIndicator = [[UIActivityIndicatorView alloc] initWithFrame:CGRectMake(0, 0, 20, 20)];
        _activityIndicatorBarButton = [[UIBarButtonItem alloc] initWithCustomView:_activityIndicator];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.screenName = @"EsploratuViewController";
    if (floor(NSFoundationVersionNumber) > NSFoundationVersionNumber_iOS_6_1) {

        self.edgesForExtendedLayout = UIRectEdgeNone;
    }
    MKUserTrackingBarButtonItem *trackingButton = [[MKUserTrackingBarButtonItem alloc] initWithMapView:self.mapView];
    self.navigationItem.rightBarButtonItem = trackingButton;
    _userCoordinate.latitude = _mapView.userLocation.coordinate.latitude;
    _userCoordinate.longitude = _mapView.userLocation.coordinate.longitude;
    
    [self centerMapInUserLocation];
}

- (void) viewWillAppear:(BOOL)animated
{
    [self checkActualLocation];
}

-(void) checkActualLocation
{
    locationManager = [[CLLocationManager alloc] init];
    locationManager.distanceFilter = kCLDistanceFilterNone;
    locationManager.desiredAccuracy = kCLLocationAccuracyBest;
    [locationManager startUpdatingLocation];
    CLLocation *ini = [locationManager location];
    [locationManager stopUpdatingLocation];
    if(ini == nil){
        
        NSLog(@"Error on location");
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Abisua" message:@"GPSak ez du zure kokapena oraindik lortu. Itxaron mesedez." delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil];
        [alert show];
    }
}


- (void)loadLekuakInDistance:(CLLocationDistance)distance
{
    [self showActivityIndicator];
    
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    [params setValue:[MintzatuAPIClient userToken] forKey:@"token"];
    [params setValue:[MintzatuAPIClient userId] forKey:@"id"];

    [params setValue:[NSNumber numberWithFloat:distance] forKey:@"distance"];
    
    CLLocationCoordinate2D centre = [_mapView centerCoordinate];
    //Actualizacion en base a la posicion del usuario
    /*
    [params setValue:[NSNumber numberWithDouble:_userCoordinate.latitude] forKey:@"lat"];
    [params setValue:[NSNumber numberWithDouble:_userCoordinate.longitude] forKey:@"lng"];
    */
    //Actualizacion en base al centro del mapa mostrado
    [params setValue:[NSNumber numberWithDouble:centre.latitude] forKey:@"lat"];
    [params setValue:[NSNumber numberWithDouble:centre.longitude] forKey:@"lng"];

    
    
    [[MintzatuAPIClient sharedClient] postPath:@"explore-places" parameters:params success:^(AFHTTPRequestOperation *operation, id responseObject) {
        NSArray *items = [responseObject objectForKey:@"places"];
        
        //Remove previous annotations
        id userLocation = [self.mapView userLocation];
        NSMutableArray *pins = [[NSMutableArray alloc] initWithArray:[self.mapView annotations]];
        if ( userLocation != nil ) {
            [pins removeObject:userLocation]; // avoid removing user location off the map
        }
        
        [self.mapView removeAnnotations:pins];
        pins = nil;
    
        for (NSDictionary *dict in items) {
            Place *place = [[Place alloc] initWithDictionary:dict];
            [_places addObject:place];
            [self addAnnotationsWithPlace:place];
        }
        
        [self hideActivityIndicator];
    } failure:nil];
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    Place *place = (Place*)sender;
    LekuaInfoViewController *lekuaInfoController = (LekuaInfoViewController*) segue.destinationViewController;
    lekuaInfoController.place = place;
}

- (void)showActivityIndicator
{
    if (self.navigationItem.leftBarButtonItem == nil) {
        [[self navigationItem] setLeftBarButtonItem:_activityIndicatorBarButton];
        [_activityIndicator startAnimating];
    }
}

- (void)hideActivityIndicator
{
    [[self navigationItem] setLeftBarButtonItem:nil];
}

#pragma mark MapView

- (BOOL)centerMapInUserLocation
{
    MKUserLocation *userLocation = self.mapView.userLocation;
    if (userLocation != nil) {
        MKCoordinateRegion userRegion = MKCoordinateRegionMake(userLocation.coordinate, MKCoordinateSpanMake(.005, .005));
        [self.mapView setRegion:userRegion animated:NO];
        return YES;
    }
    return NO;
}

- (void)addAnnotationsWithPlace:(Place*)place
{
    CLLocationCoordinate2D coordinate = CLLocationCoordinate2DMake([place.lat doubleValue], [place.lng doubleValue]);
    PlaceCategory *placeCategory = [place.kategoriak objectAtIndex:0];
    MintzatuAnnotation *annotation = [[MintzatuAnnotation alloc] initWithTitle:place.izena subtitle:place.helbidea imageURL:placeCategory.imgUrl andCoordinate:coordinate];
    annotation.place = place;
    [self.mapView addAnnotation:annotation];
}

- (void)mapView:(MKMapView *)mapView regionDidChangeAnimated:(BOOL)animated
{
    MKCoordinateRegion region = mapView.region;
    CLLocationCoordinate2D centerCoordinate = mapView.centerCoordinate;
    CLLocation * newLocation = [[CLLocation alloc] initWithLatitude:centerCoordinate.latitude+region.span.latitudeDelta longitude:centerCoordinate.longitude];
    CLLocation * centerLocation = [[CLLocation alloc] initWithLatitude:centerCoordinate.latitude longitude:centerCoordinate.longitude];
    CLLocationDistance distance = [centerLocation distanceFromLocation:newLocation];
    
    [self loadLekuakInDistance:distance + DISTANCE_MARGIN];
}

- (void)mapView:(MKMapView *)mapView didUpdateUserLocation:(MKUserLocation *)userLocation
{
    _userCoordinate = userLocation.coordinate;
}

- (MKAnnotationView *)mapView:(MKMapView *)mapView viewForAnnotation:(id)annotation
{
    MKAnnotationView *aView = nil;
    if ([annotation isKindOfClass:[MintzatuAnnotation class]]) {
        MintzatuAnnotation *mintzatuAnnotation = (MintzatuAnnotation*)annotation;
        
        aView = [[MKAnnotationView alloc] initWithAnnotation:mintzatuAnnotation reuseIdentifier:@"pinView"];
        
        aView.canShowCallout = YES;
        aView.enabled = YES;
        aView.centerOffset = CGPointMake(0, 0);
        aView.draggable = NO;
        aView.rightCalloutAccessoryView = [UIButton buttonWithType:UIButtonTypeDetailDisclosure];
        aView.annotation = mintzatuAnnotation;
        __weak MKAnnotationView *v = aView;
        UIImage *image = [UIImage imageNamed:@"AnnotationPlaceholder"];
        [aView setImageWithURL:[NSURL URLWithString:mintzatuAnnotation.imgUrl] placeholderImage:image completed:^(UIImage *image, NSError *error, SDImageCacheType cacheType) {
            CGRect frame = v.frame;
            frame.size.width = 30;
            frame.size.height = 30;
            v.frame = frame;
        }];
    }
    return aView;
}

- (void)mapView:(MKMapView *)mapView annotationView:(MKAnnotationView *)view calloutAccessoryControlTapped:(UIControl *)control
{
    MintzatuAnnotation *mintzatuAnnotation = (MintzatuAnnotation*) view.annotation;
    [self performSegueWithIdentifier:@"lekua_info" sender:mintzatuAnnotation.place];
}

@end
