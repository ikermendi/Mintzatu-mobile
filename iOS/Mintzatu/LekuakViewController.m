
//
//  LekuakViewController.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 19/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "LekuakViewController.h"

#import "LekuaCell.h"
#import "Place.h"
#import "PlaceCategory.h"
#import "LekuaInfoViewController.h"
#import "MintzatuAPIClient.h"
#import "UIImageView+WebCache.h"
#import "MKAnnotationView+WebCache.h"
#import "MintzatuAnnotation.h"
#import "JSFlatButton.h"
#import "LekuaGehituViewController.h"
#import "ZAActivityBar.h"

#import <MapKit/MapKit.h>

#define HEADER_ROW_HEIGHT 100
#define ROW_HEIGHT 56

@interface LekuakViewController() <UITableViewDataSource, UITableViewDelegate, MKMapViewDelegate>
{
    BOOL _lekuakViewHidden;
    BOOL _lekuakLoaded;
    CLLocation *_userLocation;
    CGFloat _lastContentOffset;
    CGFloat _offset;
    NSMutableArray *_places;
    CLLocationCoordinate2D centerCoordinate;
    CGPoint userPoint;
    CGFloat init;
    __weak IBOutlet NSLayoutConstraint *tableViewVerticalConstraint;
    __weak IBOutlet UIView *suPutaMadre;
    
    CLLocationManager *locationManager;
    BOOL ePos;
}
@property (strong, nonatomic) UIRefreshControl *refreshControl;
@property (weak, nonatomic) IBOutlet MKMapView *mapView;
@property (weak, nonatomic) IBOutlet UITableView *lekuakTableView;
@property (weak, nonatomic) IBOutlet UIView *loadingView;
@property (weak, nonatomic) IBOutlet JSFlatButton *closeButton;
@end

@implementation LekuakViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.screenName = @"LekuakViewController";
    init = HEADER_ROW_HEIGHT;
    
    [self.closeButton setButtonBackgroundColor:[UIColor mintzatuBlue]];
    [self.closeButton setButtonForegroundColor:[UIColor mintzatuBlue]];
    [self.closeButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.closeButton setTitleColor:[UIColor whiteColor] forState:UIControlStateHighlighted];
    
    _places = [[NSMutableArray alloc] init];
    _lekuakViewHidden = NO;
    
    
}

- (void)dealloc
{
    self.refreshControl = nil;
    self.mapView = nil;
    self.lekuakTableView = nil;
    self.loadingView = nil;
    self.closeButton = nil;
    _userLocation = nil;
    _places = nil;
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    NSIndexPath*    selection = [self.lekuakTableView indexPathForSelectedRow];
    if (selection) {
        [self.lekuakTableView deselectRowAtIndexPath:selection animated:YES];
    }

    
    [self checkActualLocation];

    
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [locationManager stopUpdatingLocation];
}

-(void) checkActualLocation
{
    locationManager = [[CLLocationManager alloc] init];
    locationManager.distanceFilter = kCLDistanceFilterNone;
    locationManager.desiredAccuracy = kCLLocationAccuracyBest;
    [locationManager startUpdatingLocation];
    CLLocation *ini = [locationManager location];
    
    if(ini == nil || ini.coordinate.latitude == 0 || ini.coordinate.longitude == 0){
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Abisua" message:@"GPSak ez du zure kokapena oraindik lortu. Itxaron mesedez." delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil];
        [alert show];

        ePos = YES;
        NSLog(@"Error on location");
        _loadingView.alpha = 0.0f;
        if(!_lekuakViewHidden){
            [self toggleLekuakView:nil];
        }
        
        
    }else{
        ePos = NO;
    }
}








- (void)change:(id)sender
{
    NSArray *selectedAnnotations = _mapView.selectedAnnotations;
    for(id annotation in selectedAnnotations) {
        [_mapView deselectAnnotation:annotation animated:NO];
    }
    
    MKUserLocation *userLocation = self.mapView.userLocation;
    MKCoordinateRegion userRegion = MKCoordinateRegionMake(userLocation.coordinate, MKCoordinateSpanMake(.005, .005));
    [self.mapView setRegion:userRegion animated:NO];
    
    userPoint = [self.mapView convertCoordinate:userLocation.coordinate toPointToView:self.mapView];
    float distanceFromTop = self.lekuakTableView.frame.size.height - HEADER_ROW_HEIGHT/2;
    userPoint.y += distanceFromTop - self.mapView.frame.size.height/2;
    centerCoordinate = [self.mapView convertPoint:userPoint toCoordinateFromView:self.mapView];
    userRegion = MKCoordinateRegionMake(centerCoordinate, MKCoordinateSpanMake(.005, .005));
    [self.mapView setRegion:userRegion animated:YES];
}


- (IBAction)toggleLekuakView:(id)sender
{
    if (ePos && _lekuakViewHidden){
        //[ZAActivityBar setLocationTabBar];
        //[ZAActivityBar showErrorWithStatus:@"Location error" duration:2.0f];
        return;
    }

    
    [UIView animateWithDuration:0.5 animations:^{
        CGRect newFrame;
        if (_lekuakViewHidden) {

                newFrame = self.lekuakTableView.frame;
                newFrame.origin.y = 0;
                CGRect madreFrame = suPutaMadre.frame;
                madreFrame.origin.y = 100;
                suPutaMadre.frame = madreFrame;

        } else {
            CGFloat p = self.mapView.frame.size.height;
            newFrame = self.lekuakTableView.frame;
            newFrame.origin.y = p;
            suPutaMadre.frame = newFrame;
        }

            self.lekuakTableView.frame = newFrame;
   
        
        
    } completion:^(BOOL finished) {
        if (_lekuakViewHidden) {
       
                [self change:nil];
                self.navigationItem.rightBarButtonItem = nil;
       
            
        } else {
            MKUserTrackingBarButtonItem *trackingButton = [[MKUserTrackingBarButtonItem alloc] initWithMapView:self.mapView];
            self.navigationItem.rightBarButtonItem = trackingButton;
            MKUserLocation *userLocation = self.mapView.userLocation;
            MKCoordinateRegion userRegion = MKCoordinateRegionMake(userLocation.coordinate, MKCoordinateSpanMake(.005, .005));
            [self.mapView setRegion:userRegion animated:YES];
        }
    
            _lekuakViewHidden = !_lekuakViewHidden;

        
    }];
}

#pragma mark Segue

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    
    if (sender != nil && ([segue.identifier compare:@"lekua_gehitu"]!=0)) {
        Place *place = (Place*)sender;
        LekuaInfoViewController *lekuaInfoController = (LekuaInfoViewController*) segue.destinationViewController;
        lekuaInfoController.place = place;
    }
}

#pragma mark Network

- (void)loadLekuak
{
    if (!_lekuakLoaded) {
        NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
        [params setValue:[MintzatuAPIClient userToken] forKey:@"token"];
        [params setValue:[MintzatuAPIClient userId] forKey:@"id"];
        [params setValue:[NSString stringWithFormat:@"%d", MAX_CHECKIN_DISTANCE] forKey:@"distance"];
        [params setValue:[NSNumber numberWithDouble:_userLocation.coordinate.latitude] forKey:@"lat"];
        [params setValue:[NSNumber numberWithDouble:_userLocation.coordinate.longitude] forKey:@"lng"];
        
        [[MintzatuAPIClient sharedClient] postPath:@"explore-places" parameters:params success:^(AFHTTPRequestOperation *operation, id responseObject) {
            NSArray *items = [responseObject objectForKey:@"places"];
            
            [_places removeAllObjects];
            
            //El servidor nos devuelve uno mas de lo solicitado. Cogemos los 20 primeros ya que el 21 sera el 1 de la siguiente peticion
            for (NSDictionary *dict in items) {
                Place *place = [[Place alloc] initWithDictionary:dict];
                [_places addObject:place];
                [self addAnnotationsWithPlace:place];
            }
            
            [UIView animateWithDuration:0.5f animations:^{
                _loadingView.alpha = 0.0f;
            }];

            [self.lekuakTableView reloadData];
            _offset = _lekuakTableView.contentSize.height - self.view.frame.size.height;
            _lekuakLoaded = YES;
            _lekuakTableView.hidden = NO;
        } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
            [self.refreshControl endRefreshing];
            double delayInSeconds = 5.0;
            dispatch_time_t popTime = dispatch_time(DISPATCH_TIME_NOW, (int64_t)(delayInSeconds * NSEC_PER_SEC));
            dispatch_after(popTime, dispatch_get_main_queue(), ^(void){
                [self loadLekuak];
            });
        }];
    }
}

- (void)addAnnotationsWithPlace:(Place*)place
{
    CLLocationCoordinate2D coordinate = CLLocationCoordinate2DMake([place.lat doubleValue], [place.lng doubleValue]);
    PlaceCategory *placeCategory = [place.kategoriak objectAtIndex:0];
    MintzatuAnnotation *annotation = [[MintzatuAnnotation alloc] initWithTitle:place.izena subtitle:place.helbidea imageURL:placeCategory.imgUrl andCoordinate:coordinate];
    annotation.place = place;
    [self.mapView addAnnotation:annotation];
}


#pragma mark Lekuak Table View

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    NSUInteger count = _places.count;
    if(count == 0){
        //Custom view if 0 places
        [tableView setSeparatorStyle:UITableViewCellSeparatorStyleNone];
        //0 for header
        //1 for add lekua
        return 2;
    }
    
    [tableView setSeparatorStyle:UITableViewCellSeparatorStyleSingleLine];
    if (count > 0) {
        count++;
    }
    //+1 por el separator
    return count + 1;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    
    if(_places.count == 0){
        if (indexPath.row == 0) {
            UITableViewCell *firstRowCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
            firstRowCell.backgroundColor = [UIColor clearColor];
            firstRowCell.selectionStyle = UITableViewCellSelectionStyleNone;
            return firstRowCell;
        }

        //Custom view if 0 places
        NSArray *nib = [[NSBundle mainBundle] loadNibNamed:@"LekuaAddCellView" owner:self options:nil];
        UITableViewCell *cell = (UITableViewCell *)[nib objectAtIndex:0];
        return cell;
    }
    
    if (indexPath.row == 0) {
        UITableViewCell *firstRowCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        firstRowCell.backgroundColor = [UIColor clearColor];
        firstRowCell.selectionStyle = UITableViewCellSelectionStyleNone;
        return firstRowCell;
    } else if (indexPath.row == 1) {
        UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"SeparatorCell"];
        if (cell == nil) {
            NSArray *nib = [[NSBundle mainBundle] loadNibNamed:@"SeparatorCellView" owner:self options:nil];
            cell = [nib objectAtIndex:0];
        }
        return cell;
    } else if (indexPath.row == _places.count+1) {
        NSArray *nib = [[NSBundle mainBundle] loadNibNamed:@"LekuaAddCellView" owner:self options:nil];
        UITableViewCell *cell = (UITableViewCell *)[nib objectAtIndex:0];
        return cell;
    } else {
        
        NSInteger index = indexPath.row - 2;
        
        LekuaCell *cell = (LekuaCell*) [tableView dequeueReusableCellWithIdentifier:@"LekuaCell"];
                
        if (cell == nil) {
            NSArray *nib = [[NSBundle mainBundle] loadNibNamed:@"LekuaCellView" owner:self options:nil];
            cell = (LekuaCell *)[nib objectAtIndex:0];
        }
        
        Place *place = [_places objectAtIndex:index];
        
        cell.nameLabel.text = place.izena;
        cell.streetLabel.text = place.helbidea;
        PlaceCategory *placeCategory = (PlaceCategory*) [place.kategoriak objectAtIndex:0];
        [cell.categoryImageView setImageWithURL:[NSURL URLWithString:placeCategory.imgUrl]];
        
        return cell;
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if(_places.count == 0){
        //Custom view if 0 places
        if (indexPath.row == 0) {
            return HEADER_ROW_HEIGHT;
        }
        return ROW_HEIGHT;
    }
    if (indexPath.row == 0) {
        return HEADER_ROW_HEIGHT;
    } else if (indexPath.row == 1) {
        return 4.0f;
    } else {
        return ROW_HEIGHT;
    }
}


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.row == 0) {
        [tableView deselectRowAtIndexPath:indexPath animated:NO];
        [self toggleLekuakView:nil];
    } else if (indexPath.row == _places.count+1) {
        [self performSegueWithIdentifier:@"lekua_gehitu" sender:nil];
    } else {
        Place *place = [_places objectAtIndex:indexPath.row - 2];
        [self performSegueWithIdentifier:@"lekua_info" sender:place];
    }
}


#pragma mark MapView

- (void)centerMapInUserLocation
{
    MKUserLocation *userLocation = self.mapView.userLocation;
    if (userLocation != nil) {
        MKCoordinateRegion userRegion = MKCoordinateRegionMake(userLocation.coordinate, MKCoordinateSpanMake(.005, .005));
        [self.mapView setRegion:userRegion animated:NO];
    }
}

- (void)mapView:(MKMapView *)mapView didFailToLocateUserWithError:(NSError *)error
{
    if (error.code == 1) {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Errorea" message:@"Aplikazioa ez dauka lokalizazio baimena" delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil];
        [alert show];
    }
}

- (void)mapView:(MKMapView *)mapView didUpdateUserLocation:(MKUserLocation *)userLocation
{
    if (userLocation.location == nil)
        return;
    if(ePos){
        if(_lekuakViewHidden){
            [self toggleLekuakView:nil];
        }
        
    }
    //ePos = NO;
    BOOL update = NO;
    if (_userLocation == nil) {
        update = YES;
         _userLocation = userLocation.location;
    } else {
        CLLocationDistance distance = [userLocation.location distanceFromLocation:_userLocation];
        if (distance > 5 * 1000) {
            update = YES;
            _lekuakLoaded = NO;
            _userLocation = userLocation.location;
        }
    }
    
    if (update) {
        [self loadLekuak];
        [self change:nil];
    }
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
        //UIImage *imagen = [UIImage imageNamed:@""];
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

#pragma mark ScrollView


- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    CGPoint p = _lekuakTableView.contentOffset;
    if (_lekuakTableView.contentOffset.y > _offset) {
        if (_lekuakTableView.contentOffset.y < 0)
            _offset = 0;
        p.y = _offset;
        _lekuakTableView.contentOffset = p;
    }
    
    
    if (scrollView.contentOffset.y < 0) {
        CGRect frame = suPutaMadre.frame;
        frame.origin.y = 100 - scrollView.contentOffset.y;
        suPutaMadre.frame = frame;
    } else {
        //mapview
        init += scrollView.contentOffset.y;
        
        if (userPoint.y > 0) {
            userPoint = [self.mapView convertCoordinate:_userLocation.coordinate toPointToView:self.mapView];
            float distanceFromTop = self.lekuakTableView.frame.size.height + scrollView.contentOffset.y - init/2;
            init -= scrollView.contentOffset.y;
            userPoint.y += distanceFromTop - self.mapView.frame.size.height/2;
            centerCoordinate = [self.mapView convertPoint:userPoint toCoordinateFromView:self.mapView];
            [self.mapView setCenterCoordinate:centerCoordinate];
        }
    }
}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView
{
        CGRect frame = suPutaMadre.frame;
        frame.origin.y = 100;
        suPutaMadre.frame = frame;
}

@end
