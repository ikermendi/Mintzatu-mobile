//
//  CheckinViewController.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 30/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "UserCheckinViewController.h"

#import "Place.h"
#import "PlaceCategory.h"
#import "MintzatuAPIClient.h"
#import "ProfileLekuaCell.h"
#import "UIImageView+WebCache.h"
#import "LekuaInfoViewController.h"
#import "MintzatuTableView.h"

@interface UserCheckinViewController ()
{
    UIView *_loadingView;
    NSMutableArray *_checkins;
    NSUInteger _page;
}
@property (weak, nonatomic) IBOutlet MintzatuTableView *tableView;
@end

@implementation UserCheckinViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.screenName = @"UserCheckinViewController";
    _checkins = [[NSMutableArray alloc] init];
    _page = 1;
    _tableViewState = TableViewNotLoaded;
    
    ((MintzatuTableView*)(self.tableView)).emptyText = @"Oraindik ez duzu check-in egin";
    
    NSArray *nib = [[NSBundle mainBundle] loadNibNamed:@"LoadingView" owner:nil options:nil];
    _loadingView = [nib objectAtIndex:0];
    [self.tableView addSubview:_loadingView];
    
    [self activateTableView:NO];
    [self loadData];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    NSIndexPath*    selection = [self.tableView indexPathForSelectedRow];
    if (selection) {
        [self.tableView deselectRowAtIndexPath:selection animated:YES];
    }
}

- (void)dealloc
{
    _loadingView = nil;
    _checkins = nil;
}

- (void)activateTableView:(BOOL)activate
{
    //Quitamos el scroll para que al cargar no se muestren
    self.tableView.scrollEnabled = activate;
    self.tableView.userInteractionEnabled = activate;
}

- (void)loadData
{
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    [params setValue:[MintzatuAPIClient userId] forKey:@"id"];
    [params setValue:[MintzatuAPIClient userToken] forKey:@"token"];
    [params setValue:[NSString stringWithFormat:@"%d", _page] forKey:@"page"];
    [params setValue:@"20" forKey:@"items"];

    [[MintzatuAPIClient sharedClient] postPath:@"my-checkins" parameters:params success:^(AFHTTPRequestOperation *operation, id responseObject) {
        NSArray *items = [responseObject objectForKey:@"checkins"];
        
        if (![items isKindOfClass:[NSNull class]]) {
            //El servidor nos devuelve uno mas de lo solicitado. Cogemos los 20 primeros ya que el 21 sera el 1 de la siguiente peticion
            for (int i = 0; i<20 && i<items.count; i++) {
                NSDictionary *placeDict = [items objectAtIndex:i];
                Place *place = [[Place alloc] init];
                place.idLekua = [placeDict objectForKey:@"id_lekua"];
                place.izena = [placeDict objectForKey:@"izena"];
                place.helbidea = [placeDict objectForKey:@"dir"];
                place.deskribapena = [placeDict objectForKey:@"deskribapena"];
                place.irudia = [placeDict objectForKey:@"irudia"];
                
                PlaceCategory *cat = [[PlaceCategory alloc] init];
                cat.identifier = [placeDict objectForKey:@"idCat"];
                cat.imgUrl = [placeDict objectForKey:@"imgCat"];
                
                place.kategoriak = (NSMutableArray*) @[cat];
                
                [_checkins addObject:place];
            }
            
            
            if (items.count <= 20) {
                _tableViewState = TableViewFinished;
            } else {
                _tableViewState = TableViewLoaded;
                _page++;
            }
            
            [self.tableView reloadData];
        }        
        
        if (_loadingView != nil) {
            [UIView animateWithDuration:1.0f animations:^{
                _loadingView.alpha = 1.0f;
            } completion:^(BOOL finished) {
                [_loadingView removeFromSuperview];
                _loadingView = nil;
                [self activateTableView:YES];
            }];
        }
    } failure:nil];
}


#pragma mark Segue

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    NSIndexPath *indexPath = (NSIndexPath*)sender;
    Place *place = [_checkins objectAtIndex:indexPath.row];
    
    LekuaInfoViewController *lekuaInfoController = (LekuaInfoViewController*) segue.destinationViewController;
    lekuaInfoController.place = place;
}


#pragma mark - TableView

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [self performSegueWithIdentifier:@"lekua_info" sender:indexPath];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if (_tableViewState == TableViewFinished) {
        return _checkins.count;
    } else if (_tableViewState == TableViewLoaded) {
        return _checkins.count + 1;
    } else {
        return 0;
    }
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (_tableViewState == TableViewLoaded) {
        if (indexPath.row == _checkins.count) {
            UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"ProfileLekuaLoadingCell"];
            cell.userInteractionEnabled = NO;
            return cell;
        }
    }    
    
    ProfileLekuaCell *cell = [tableView dequeueReusableCellWithIdentifier:@"ProfileLekuaCell"];
    
    cell.borderWidth = 1.0f;
    cell.borderColor = [UIColor backgroundBeige];
    
    if (indexPath.row == 0) {
        cell.type = SimpleCellTypeTop;
    } else if (indexPath.row == _checkins.count) {
        cell.type = SimpleCellTypeBottom;
    } else {
        cell.type = SimpleCellTypeMiddle;
    }
    
    Place *place = [_checkins objectAtIndex:indexPath.row];
    
    cell.nameLabel.text = place.izena;
    cell.streetLabel.text = place.helbidea;
    PlaceCategory *category = [place.kategoriak objectAtIndex:0];
    [cell.categoryImageView setImageWithURL:[NSURL URLWithString:category.imgUrl]];
    
    return cell;
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (_tableViewState != TableViewFinished && indexPath.row == _checkins.count-1) {
        [self loadData];
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 56.0f;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    UIView *v = [[UIView alloc] init];
    v.backgroundColor = [UIColor clearColor];
    return v;
}

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section
{
    UIView *v = [[UIView alloc] init];
    v.backgroundColor = [UIColor clearColor];
    return v;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    return 10.0f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section
{
    return 10.0f;
}

@end
