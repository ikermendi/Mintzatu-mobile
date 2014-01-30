//
//  LekuaGehituViewController.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 05/09/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "LekuaGehituViewController.h"

#import "MintzatuAPIClient.h"
#import "DetailCell.h"
#import "MintzatuAnnotation.h"
#import "ZAActivityBar.h"
#import "JSFlatButton.h"
#import "MBProgressHUD.h"
#import "Place.h"
#import "LekuaInfoViewController.h"

#import <MapKit/MapKit.h>

@interface LekuaGehituViewController () <MKMapViewDelegate, CLLocationManagerDelegate, UITableViewDataSource, UITableViewDelegate, UIPickerViewDataSource, UIPickerViewDelegate, UITextFieldDelegate>
{
    NSMutableArray *_categories;
    UIActionSheet *_actionSheet;
    NSString *_selectedCategory;
    NSString *_fullAddress;
    NSString *_address;
    NSString *_locality;
    NSString *_name;
    CLLocationCoordinate2D _coordinate;
    MintzatuAnnotation *_annotation;
    CLLocationManager *_locationManager;
    BOOL _locationLoaded;
}
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (weak, nonatomic) IBOutlet MKMapView *mapView;
@end

@implementation LekuaGehituViewController


- (void)viewDidLoad
{
    [super viewDidLoad];
    self.screenName = @"LekuakGehituViewController";
    self.mapView.layer.shadowColor = [UIColor blackColor].CGColor;
    CGPathRef path = [UIBezierPath bezierPathWithRect:self.mapView.bounds].CGPath;
    self.mapView.layer.shadowPath = path;
    self.mapView.layer.shadowOpacity = 0.3f;
    self.mapView.layer.shadowOffset = CGSizeMake(1.0f, 1.0f);
    self.mapView.layer.shadowRadius = 1.0f;
    self.mapView.layer.masksToBounds = NO;
    
    _locationLoaded = NO;
    _categories = [[NSMutableArray alloc] init];
    _locationManager = [[CLLocationManager alloc] init];
    _locationManager.delegate = self;
    [_locationManager startUpdatingLocation];
}


- (void)dealloc
{
    [_locationManager startUpdatingLocation];
    _locationManager = nil;
}

- (void)getCategories
{
    if (_categories.count == 0) {
        MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
        hud.labelText = @"Kategoriak kargatzen";
        NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
        [params setValue:[MintzatuAPIClient userToken] forKey:@"token"];
        [params setValue:[MintzatuAPIClient userId] forKey:@"id"];
        [[MintzatuAPIClient sharedClient] postPath:@"get-categories" parameters:params success:^(AFHTTPRequestOperation *operation, id responseObject) {
            _categories = [responseObject objectForKey:@"categories"];
            [hud hide:YES];
            
            DetailCell *cell = (DetailCell*) [self.tableView cellForRowAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:0]];
            _name = cell.detailTextField.text;
            [cell.detailTextField resignFirstResponder];
            
            cell = (DetailCell*) [self.tableView cellForRowAtIndexPath:[NSIndexPath indexPathForRow:2 inSection:0]];
            [cell.detailTextField resignFirstResponder];
            _address = cell.detailTextField.text;
            
            [self showActionSheet];
            _selectedCategory = [[_categories objectAtIndex:0] objectForKey:@"name"];
        } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
            [hud hide:YES];
            [self getCategories];
        }];
    } else {
        [self showActionSheet];
    }
}

- (void)showActionSheet
{
    _actionSheet = [[UIActionSheet alloc] initWithTitle:nil delegate:nil cancelButtonTitle:nil destructiveButtonTitle:nil otherButtonTitles:nil];
    
    CGRect pickerFrame = CGRectMake(0, 0, 0, 0);
    UIPickerView *pickerView = [[UIPickerView alloc] initWithFrame:pickerFrame];
    pickerView.showsSelectionIndicator = YES;
    pickerView.dataSource = self;
    pickerView.delegate = self;
    [_actionSheet addSubview:pickerView];
    
    UISegmentedControl *closeButton = [[UISegmentedControl alloc] initWithItems:[NSArray arrayWithObject:@"Ok"]];
    closeButton.momentary = YES;
    closeButton.frame = CGRectMake(260, 7.0f, 50.0f, 30.0f);
    closeButton.segmentedControlStyle = UISegmentedControlStyleBar;
    closeButton.tintColor = [UIColor blackColor];
    [closeButton addTarget:self action:@selector(dismissActionSheet) forControlEvents:UIControlEventValueChanged];
    [_actionSheet addSubview:closeButton];
    [_actionSheet showFromTabBar:self.tabBarController.tabBar];
    [_actionSheet setBounds:CGRectMake(0, 0, 320, 485)];
    [_actionSheet showFromTabBar:self.tabBarController.tabBar];
    [_actionSheet setBounds:CGRectMake(0, 0, 320, 415)];
}

- (void)dismissActionSheet
{
    if (_selectedCategory.length == 0) {
        _selectedCategory = [[_categories objectAtIndex:0] objectForKey:@"name"];
    }
    
    [_actionSheet dismissWithClickedButtonIndex:0 animated:YES];
    [self.tableView reloadData];
}

- (IBAction)lekuaGehitu:(id)sender
{
    DetailCell *nameCell = (DetailCell*) [self.tableView cellForRowAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:0]];
    DetailCell *categoryCell = (DetailCell*) [self.tableView cellForRowAtIndexPath:[NSIndexPath indexPathForRow:1 inSection:0]];
    DetailCell *addressCell = (DetailCell*) [self.tableView cellForRowAtIndexPath:[NSIndexPath indexPathForRow:2 inSection:0]];

    _name = nameCell.detailTextField.text;
    _selectedCategory = categoryCell.detailTextField.text;
    _address = addressCell.detailTextField.text;

    if (_name.length == 0 || _selectedCategory.length == 0 || _fullAddress.length == 0) {
        [ZAActivityBar setLocationTabBar];
        [ZAActivityBar showErrorWithStatus:@"Datu guztiak beharrezkoak dira" duration:2.0f];
        return;
    }
    
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    [params setValue:[MintzatuAPIClient userId] forKey:@"id"];
    [params setValue:[MintzatuAPIClient userToken] forKey:@"token"];
    [params setValue:_name forKey:@"name"];
    [params setValue:_address forKey:@"address"];
    [params setValue:_locality forKey:@"town"];
    
    NSString *catId;
    for (NSDictionary *categoryDict in _categories) {
        if ([[categoryDict objectForKey:@"name"] isEqualToString:_selectedCategory]) {
            catId = [categoryDict objectForKey:@"id"];
            break;
        }
    }
    
    [params setValue:catId forKey:@"katId"];
    [params setValue:[NSString stringWithFormat:@"%f", _coordinate.latitude] forKey:@"lat"];
    [params setValue:[NSString stringWithFormat:@"%f", _coordinate.longitude] forKey:@"lng"];

    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    hud.labelText = @"Lekua gehitzen";
    [[MintzatuAPIClient sharedClient] postPath:@"new-place" parameters:params success:^(AFHTTPRequestOperation *operation, id responseObject) {
        Place *place = [[Place alloc] initWithDictionary:[responseObject objectForKey:@"place"]];
        [self performSegueWithIdentifier:@"lekua_info" sender:place];
        [hud hide:YES];
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        [hud hide:YES];
    }];
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    LekuaInfoViewController *controller = (LekuaInfoViewController*) segue.destinationViewController;
    controller.place = (Place*) sender;
}


#pragma mark Textfield delegate

- (BOOL)textFieldShouldBeginEditing:(UITextField *)textField
{
    if (textField.tag == 0) {
        [self getCategories];
        return NO;
    } else {
        return YES;
    }
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    if (textField.tag == 1) {
        [textField resignFirstResponder];
        _fullAddress = textField.text;
        [self reverseGeocodeAddress:textField.text];
        return YES;
    } else if (textField.tag == 2) {
        [textField resignFirstResponder];
        _name = textField.text;
        return YES;
    } else {
        return NO;
    }
}


#pragma mark MapKit and Location

- (void)locationManager:(CLLocationManager *)manager
	didUpdateToLocation:(CLLocation *)newLocation
		   fromLocation:(CLLocation *)oldLocation
{
    if (!_locationLoaded) {
        [self reverseGeocodeLocation:newLocation];
        _locationLoaded = YES;
    }

}

- (void)mapView:(MKMapView *)mapView annotationView:(MKAnnotationView *)view didChangeDragState:(MKAnnotationViewDragState)newState
   fromOldState:(MKAnnotationViewDragState)oldState
{
    if (newState == MKAnnotationViewDragStateEnding) {
        CLLocationCoordinate2D droppedAt = view.annotation.coordinate;
        [self reverseGeocodeLocation:[[CLLocation alloc] initWithLatitude:droppedAt.latitude longitude:droppedAt.longitude]];
    }
}


- (MKAnnotationView *)mapView:(MKMapView *)mapView viewForAnnotation:(id)annotation
{
    MKPinAnnotationView *pinView = [[MKPinAnnotationView alloc] initWithAnnotation:annotation reuseIdentifier:@"pinView"];
    
    pinView.annotation = annotation;
    pinView.draggable = YES;
    pinView.animatesDrop = YES;
    
    return pinView;
}

- (void)reverseGeocodeAddress:(NSString*)address
{
    CLGeocoder *geocoder = [[CLGeocoder alloc] init];
    [geocoder geocodeAddressString:address completionHandler:^(NSArray *placemarks, NSError *error) {
        if(!error){
            [self updateLocationWithPlacemark:[placemarks lastObject]];
        } else {
            [ZAActivityBar setLocationTabBar];
            [ZAActivityBar showErrorWithStatus:@"Helbidea ez da existitzen" duration:1.5f];
        }
    }];
}

- (void)reverseGeocodeLocation:(CLLocation*)location
{
    CLGeocoder *geocoder = [[CLGeocoder alloc] init];
    [geocoder reverseGeocodeLocation:location completionHandler:^(NSArray *placemarks, NSError *error) {
        if(!error){
            [self updateLocationWithPlacemark:[placemarks lastObject]];
        }
    }];
}

- (void)updateLocationWithPlacemark:(CLPlacemark*)placemark
{
    NSString *number = placemark.subThoroughfare;
    if (number == nil) {
        _fullAddress = [NSString stringWithFormat:@"%@, %@", placemark.thoroughfare, placemark.locality];
        _address = placemark.thoroughfare;
    } else {
        _fullAddress = [NSString stringWithFormat:@"%@ %@, %@", placemark.thoroughfare, number, placemark.locality];
        _address = [NSString stringWithFormat:@"%@ %@", placemark.thoroughfare, number];
    }
    
    _locality = placemark.locality;
    _coordinate = placemark.location.coordinate;
    
    if (_annotation == nil) {
        _annotation = [[MintzatuAnnotation alloc] initWithTitle:nil subtitle:nil imageURL:nil andCoordinate:placemark.location.coordinate];
    } else {
        _annotation.coordinate = placemark.location.coordinate;
    }
    MKCoordinateRegion region = MKCoordinateRegionMake(placemark.location.coordinate, MKCoordinateSpanMake(.003, .003));
    [self.mapView setRegion:region animated:NO];
    [self.mapView addAnnotation:_annotation];
    [self.tableView reloadData];
}

#pragma mark TableView

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 3;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    DetailCell *cell = [tableView dequeueReusableCellWithIdentifier:@"DetailCell"];
    
    NSUInteger row = indexPath.row;
    
    if (row == 0) {
        cell.type = SimpleCellTypeTop;
        cell.infoLabel.text = @"Izena";
        cell.detailTextField.placeholder = @"Lekuaren izena";
        cell.detailTextField.tag = 2;
        cell.detailTextField.text = _name;
    } else if (row == 2) {
        cell.type = SimpleCellTypeBottom;
        cell.infoLabel.text = @"Helbidea";
        cell.detailTextField.tag = 1;
        cell.detailTextField.placeholder = @"Pin-a mugitu helbidea lortzeko";
        cell.detailTextField.text = _fullAddress;
    } else {
        cell.type = SimpleCellTypeMiddle;
        cell.infoLabel.text = @"Kategoria";
        cell.detailTextField.placeholder = @"Kategoria aukeratu";
        cell.detailTextField.tag = 0;
        cell.detailTextField.text = _selectedCategory;
    }
    
    cell.detailTextField.delegate = self;
    
    cell.borderColor = [UIColor backgroundBeige];
    cell.borderWidth = 1.0f;
    
    return cell;
}

#pragma mark Picker

- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView
{
    return 1;
}


- (NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component
{
    return _categories.count;
}

- (NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component
{
    return [[_categories objectAtIndex:row] objectForKey:@"name"];
}

- (void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component
{
    dispatch_async(dispatch_get_main_queue(), ^{
        _selectedCategory = [[_categories objectAtIndex:row] objectForKey:@"name"];
        [self.tableView reloadData];
    });
}


@end
