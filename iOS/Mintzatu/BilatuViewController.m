//
//  BilatuViewController.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 11/09/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "BilatuViewController.h"

#import "MintzatuAPIClient.h"
#import "MintzatuTableView.h"
#import "LekuaCell.h"
#import "Place.h"
#import "PlaceCategory.h"
#import "LagunaCell.h"
#import "BilatuTextField.h"
#import "LekuaInfoViewController.h"
#import "LagunaProfileViewController.h"
#import "UIImageView+WebCache.h"
#import "ZAActivityBar.h"
#import "CategoryCell.h"

typedef enum {
    PeopleSearchType,
    PlacesSearchType,
    CategorySearchType
} SearchType;

@interface BilatuViewController () <UITextFieldDelegate, UITableViewDataSource, UITableViewDelegate>
{
    NSMutableArray *_results;
    SearchType _searchType;
    IBOutlet UITapGestureRecognizer *_tapGesture;
    NSMutableArray *_categories;
}

@property (weak, nonatomic) IBOutlet MintzatuTableView *tableView;
@property (weak, nonatomic) IBOutlet UIActivityIndicatorView *searchIndicator;
@property (weak, nonatomic) IBOutlet UISegmentedControl *segmentedControl;
@property (weak, nonatomic) IBOutlet BilatuTextField *searchTextField;
@end

@implementation BilatuViewController

- (id)initWithCoder:(NSCoder *)aDecoder
{
    self = [super initWithCoder:aDecoder];
    if (self) {
        _results = [[NSMutableArray alloc] init];
    }
    return self;
}

- (void)dealloc
{
    self.tableView = nil;
    self.searchIndicator = nil;
    _results = nil;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    [self loadCategories];
    self.screenName = @"BilatuViewController";
    _tapGesture.enabled = NO;
    
    if (floor(NSFoundationVersionNumber) > NSFoundationVersionNumber_iOS_6_1) {
        self.edgesForExtendedLayout = NO;
        CGRect segmentedControlFrame = _segmentedControl.frame;
        segmentedControlFrame.size.height = 30;
        _segmentedControl.frame = segmentedControlFrame;
    }
        
    ((MintzatuTableView*)_tableView).emptyText = @"Ez da ezer aurkitu";
    
    [_segmentedControl addTarget:self action:@selector(segmentChanged:) forControlEvents:UIControlEventValueChanged];
    
    _searchTextField.layer.borderColor = [UIColor backgroundBeige].CGColor;
    _searchTextField.layer.borderWidth = 1.0f;
    
    _searchType = CategorySearchType;
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardShow) name:UIKeyboardDidShowNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardHide) name:UIKeyboardDidHideNotification object:nil];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    NSIndexPath*    selection = [self.tableView indexPathForSelectedRow];
    if (selection) {
        [self.tableView deselectRowAtIndexPath:selection animated:YES];
    }
}

- (void)keyboardShow
{
    _tapGesture.enabled = YES;
}

- (void)keyboardHide
{
    _tapGesture.enabled = NO;
}

- (void)loadCategories
{
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    [params setValue:[MintzatuAPIClient userToken] forKey:@"token"];
    [params setValue:[MintzatuAPIClient userId] forKey:@"id"];
    self.searchIndicator.hidden = NO;
    [[MintzatuAPIClient sharedClient] postPath:@"get-categories" parameters:params success:^(AFHTTPRequestOperation *operation, id responseObject) {
        _categories = [[NSMutableArray alloc] initWithArray:[responseObject objectForKey:@"categories" ]];
        _searchType = CategorySearchType;
        [_results removeAllObjects];
        self.searchIndicator.hidden = YES;
        [_tableView reloadData];
        
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        [self loadCategories];
    }];
}

- (void)searchPeople
{
    NSString *text = _searchTextField.text;
    
    if (text.length == 0) {
        [_results removeAllObjects];
        [self.tableView reloadData];
        return;
    }
    
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    [params setValue:[MintzatuAPIClient userToken] forKey:@"token"];
    [params setValue:[MintzatuAPIClient userId] forKey:@"id"];
    [params setValue:@"50" forKey:@"items"];
    [params setValue:@"1" forKey:@"page"];
    [params setValue:text forKey:@"subject"];
    
    self.searchIndicator.hidden = NO;
    
    [[MintzatuAPIClient sharedClient] postPath:@"search-people" parameters:params success:^(AFHTTPRequestOperation *operation, id responseObject) {
        _results = [[NSMutableArray alloc] initWithArray:[responseObject objectForKey:@"places" ]];
        [self.tableView reloadData];
        self.searchIndicator.hidden = YES;
    } failure:nil];
    
}

- (void)searchPlace
{
    NSString *text = _searchTextField.text;
    
    if (text.length == 0) {
        [_results removeAllObjects];
        [self.tableView reloadData];
        return;
    }
    
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    [params setValue:[MintzatuAPIClient userToken] forKey:@"token"];
    [params setValue:[MintzatuAPIClient userId] forKey:@"id"];
    [params setValue:@"0" forKey:@"lng"];
    [params setValue:@"0" forKey:@"lat"];
    [params setValue:@"50" forKey:@"items"];
    [params setValue:@"1" forKey:@"page"];
    [params setValue:text forKey:@"subject"];
    
    self.searchIndicator.hidden = NO;
    
    [[MintzatuAPIClient sharedClient] postPath:@"search-place" parameters:params success:^(AFHTTPRequestOperation *operation, id responseObject) {
        _results = [[NSMutableArray alloc] initWithArray:[responseObject objectForKey:@"places" ]];
        [self.tableView reloadData];
        self.searchIndicator.hidden = YES;
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        self.searchIndicator.hidden = YES;
    }];
    
}

- (void)searchPlaceByCategory:(NSInteger)idKat
{
    
    [_segmentedControl setSelectedSegmentIndex:1];
    
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    [params setValue:[MintzatuAPIClient userToken] forKey:@"token"];
    [params setValue:[MintzatuAPIClient userId] forKey:@"id"];
    [params setValue:@"0" forKey:@"lng"];
    [params setValue:@"0" forKey:@"lat"];
    [params setValue:@"50" forKey:@"items"];
    [params setValue:@"1" forKey:@"page"];
    [params setValue:[NSString stringWithFormat:@"%d", idKat] forKey:@"idKategoria"];
    
    NSString *text = _searchTextField.text;
    if (!text) {
        text = @"";
    }
    [params setValue:text forKey:@"subject"];
    self.searchIndicator.hidden = NO;
    
    [[MintzatuAPIClient sharedClient] postPath:@"search-place" parameters:params success:^(AFHTTPRequestOperation *operation, id responseObject) {
        _results = [[NSMutableArray alloc] initWithArray:[responseObject objectForKey:@"places" ]];
        _searchType = PlacesSearchType;
        [self.tableView reloadData];
        self.searchIndicator.hidden = YES;
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        self.searchIndicator.hidden = YES;
    }];
    
}



- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    if (textField.text.length < 2) {
        [ZAActivityBar setLocationTabBar];
        [ZAActivityBar showErrorWithStatus:@"Minimo bi karaktere beharrezkoak dira" duration:2.0f];
        return NO;
    } else {
        [textField resignFirstResponder];
        if (_searchType == PeopleSearchType)
            [self searchPeople];
        else if (_searchType == PlacesSearchType)
            [self searchPlace];
        
        return YES;
    }
}

- (IBAction)hideKeyboard:(id)sender
{
    [self.view endEditing:YES];
}

- (void)segmentChanged:(id)sender
{
    if (_segmentedControl.selectedSegmentIndex == 0) {
        _searchType = CategorySearchType;
        [_results removeAllObjects];
        self.searchIndicator.hidden = YES;
        [_tableView reloadData];
    } else if (_segmentedControl.selectedSegmentIndex == 1) {
        _searchType = PlacesSearchType;
        [self searchPlace];
    } else if (_segmentedControl.selectedSegmentIndex == 2) {
        _searchType = PeopleSearchType;
        [self searchPeople];
        
    }
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    NSIndexPath *indexPath = (NSIndexPath*)sender;
    NSDictionary *searchDict = [_results objectAtIndex:indexPath.row];
    
    if ([segue.identifier isEqualToString:@"pertsona"]) {
        LagunaProfileViewController *controller = (LagunaProfileViewController*) segue.destinationViewController;
        controller.profileId = [[searchDict objectForKey:@"id"] integerValue];
    } else {
        LekuaInfoViewController *controller = (LekuaInfoViewController*) segue.destinationViewController;
        Place *place = [[Place alloc] init];
        place.deskribapena = [searchDict objectForKey:@"deskribapena"];
        place.idLekua = [searchDict objectForKey:@"id_lekua"];
        place.irudia = [searchDict objectForKey:@"irudia"];
        place.izena = [searchDict objectForKey:@"izena"];
        place.helbidea = [searchDict objectForKey:@"helbidea"];
        place.lat = [searchDict objectForKey:@"helbideaLat"];
        place.lng = [searchDict objectForKey:@"helbideaLng"];
        place.url = [searchDict objectForKey:@"url"];
        
        PlaceCategory *cat = [[PlaceCategory alloc] init];
        cat.izena = [searchDict objectForKey:@"kategoriaIzena"];
        place.kategoriak = [[NSMutableArray alloc] init];
        [place.kategoriak addObject:cat];
        
        controller.place = place;
    }
}

-(NSString*) checkForNull:(NSString*)cad
{
    if ([cad isEqual:[NSNull null]]) {
        cad = @"";
    }else if ([cad isEqualToString:@""]){
        cad = @"";
    }
    return cad;
}


#pragma mark TableView

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if(_searchType == CategorySearchType)
        return _categories.count;
    return _results.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSDictionary *searchDict;
    
    if(_searchType == CategorySearchType){
        searchDict = [_categories objectAtIndex:indexPath.row];
    }else {
        searchDict = [_results objectAtIndex:indexPath.row];
    }
    
    if (_searchType == PeopleSearchType) {
        LagunaCell *cell = [tableView dequeueReusableCellWithIdentifier:@"LagunaCell"];
        cell.nameLabel.text = [searchDict objectForKey:@"username"];
        [cell.photoImageView setImageWithURL:[NSURL URLWithString:[searchDict objectForKey:@"userImage"]]];
        
        cell.borderWidth = 1.0f;
        cell.borderColor = [UIColor backgroundBeige];
        
        if (indexPath.row == 0) {
            cell.type = SimpleCellTypeTop;
        } else if (indexPath.row == _results.count-1) {
            cell.type = SimpleCellTypeBottom;
        } else {
            cell.type = SimpleCellTypeMiddle;
        }
        
        return cell;
    } else if (_searchType == PlacesSearchType) {
        LekuaCell *cell = [tableView dequeueReusableCellWithIdentifier:@"LekuaCell"];
        cell.nameLabel.text = [searchDict objectForKey:@"izena"];
        [cell.categoryImageView setImageWithURL:[NSURL URLWithString:[searchDict objectForKey:@"katImgUrl"]]];
        
        NSString *temp = [searchDict objectForKey:@"helbidea"];
        temp = [self checkForNull:temp];
        cell.streetLabel.text = temp;

        
        cell.borderWidth = 1.0f;
        cell.borderColor = [UIColor backgroundBeige];
        
        if (indexPath.row == 0) {
            cell.type = SimpleCellTypeTop;
        } else if (indexPath.row == _results.count-1) {
            cell.type = SimpleCellTypeBottom;
        } else {
            cell.type = SimpleCellTypeMiddle;
        }
        
        return cell;
    } else {
        CategoryCell *cell = [tableView dequeueReusableCellWithIdentifier:@"CategoryCell"];
        
        cell.lblCategory.text = [searchDict objectForKey:@"name"];
        [cell.imgCategory setImageWithURL:[NSURL URLWithString:[searchDict objectForKey:@"imgCat"]]];
        cell.tag = [[searchDict objectForKey:@"id"] integerValue];
        if (indexPath.row == 0) {
            cell.type = SimpleCellTypeTop;
        } else if (indexPath.row == _results.count-1) {
            cell.type = SimpleCellTypeBottom;
        } else {
            cell.type = SimpleCellTypeMiddle;
        }
        
        return cell;
    }
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (_searchType == PeopleSearchType)
        [self performSegueWithIdentifier:@"pertsona" sender:indexPath];
    else if (_searchType == PlacesSearchType)
        [self performSegueWithIdentifier:@"lekua" sender:indexPath];
    else if (_searchType == CategorySearchType){
        NSLog(@"Category click");
        NSInteger pos = indexPath.row;

        NSDictionary *cat = [_categories objectAtIndex:pos];
        NSInteger idKat = [[cat objectForKey:@"id"] integerValue];
        [self searchPlaceByCategory:idKat];
    }
    
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 56.0f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    return 10.0f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section
{
    return 10.0f;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    UIView *view = [[UIView alloc] init];
    view.backgroundColor = [UIColor clearColor];
    return view;
}

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section
{
    UIView *view = [[UIView alloc] init];
    view.backgroundColor = [UIColor clearColor];
    return view;
}

@end
