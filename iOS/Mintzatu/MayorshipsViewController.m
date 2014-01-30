//
//  MayorshipsViewController.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 02/09/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "MayorshipsViewController.h"

#import "ProfileLekuaCell.h"
#import "MintzatuAPIClient.h"
#import "Place.h"
#import "PlaceCategory.h"
#import "LekuaInfoViewController.h"
#import "UIImageView+WebCache.h"
#import "MintzatuTableView.h"

@interface MayorshipsViewController ()
{
    UIView *_loadingView;
    NSMutableArray *_mayorships;
}
@property (weak, nonatomic) IBOutlet MintzatuTableView *tableView;
@end

@implementation MayorshipsViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.screenName = @"MayorshipsViewController";
    _mayorships = [[NSMutableArray alloc] init];
    
    ((MintzatuTableView*)(self.tableView)).emptyText = @"Ez dago alkatetzarik erakusteko";
    
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
    _mayorships = nil;
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
    [params setValue:[NSNumber numberWithInteger:_profileId] forKey:@"idProfile"];
    
    [[MintzatuAPIClient sharedClient] postPath:@"get-mayorships" parameters:params success:^(AFHTTPRequestOperation *operation, id responseObject) {
        NSDictionary *dict = [responseObject objectForKey:@"places"];
        
        if (![dict isKindOfClass:[NSNull class]]) {
            for (NSDictionary *placeDict in dict) {
                Place *place = [[Place alloc] init];
                place.idLekua = [placeDict objectForKey:@"id_lekua"];
                place.izena = [placeDict objectForKey:@"name"];
                place.helbidea = [placeDict objectForKey:@"address"];
                place.deskribapena = [placeDict objectForKey:@"deskribapena"];
                place.irudia = [placeDict objectForKey:@"irudia"];
                
                PlaceCategory *cat = [[PlaceCategory alloc] init];
                cat.imgUrl = [placeDict objectForKey:@"imgCat"];
                
                place.kategoriak = (NSMutableArray*) @[cat];
                
                [_mayorships addObject:place];
            }
            
            [self.tableView reloadData];
        }
        
        [UIView animateWithDuration:1.0f animations:^{
            _loadingView.alpha = 1.0f;
        } completion:^(BOOL finished) {
            [_loadingView removeFromSuperview];
            [self activateTableView:YES];
        }];
    } failure:nil];
}


#pragma mark Segue

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    NSIndexPath *indexPath = (NSIndexPath*)sender;
    Place *place = [_mayorships objectAtIndex:indexPath.row];
    
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
    return _mayorships.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    ProfileLekuaCell *cell = [tableView dequeueReusableCellWithIdentifier:@"ProfileLekuaCell"];
    
    cell.borderWidth = 1.0f;
    cell.borderColor = [UIColor backgroundBeige];
    
    if (indexPath.row == 0) {
        cell.type = SimpleCellTypeTop;
    } else if (indexPath.row == _mayorships.count) {
        cell.type = SimpleCellTypeBottom;
    } else {
        cell.type = SimpleCellTypeMiddle;
    }
    
    Place *place = [_mayorships objectAtIndex:indexPath.row];
    
    cell.nameLabel.text = place.izena;
    cell.streetLabel.text = place.helbidea;
    PlaceCategory *category = [place.kategoriak objectAtIndex:0];
    [cell.categoryImageView setImageWithURL:[NSURL URLWithString:category.imgUrl]];
    
    return cell;
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

@end
