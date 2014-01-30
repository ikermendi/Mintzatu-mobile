//
//  LagunaProfileViewController.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 03/09/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "LagunaProfileViewController.h"

#import "MintzatuAPIClient.h"
#import "ProfileDataCell.h"
#import "Friend.h"
#import "AwardButton.h"
#import "AwardsCell.h"
#import "MBProgressHUD.h"
#import "UIImageView+WebCache.h"
#import "BadgesViewController.h"
#import "MayorshipsViewController.h"
#import "GAITrackedViewController.h"
#import "GAI.h"
#import "GAIDictionaryBuilder.h"
#import "GAIFields.h"

@interface LagunaProfileViewController ()
{
    Friend *_friend;
    UIView *_loadingView;
    BOOL _showFriendshipCell;
    BOOL _showFriendRequestedCell;
}
@end

@implementation LagunaProfileViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    id tracker = [GAI sharedInstance].defaultTracker;
    [tracker set:kGAIScreenName value:@"LagunaProfileViewController"];
    [tracker send:[[GAIDictionaryBuilder createAppView]  build]];
    [self.refreshControl addTarget:self action:@selector(loadData) forControlEvents:UIControlEventValueChanged];
    
    NSArray *nib = [[NSBundle mainBundle] loadNibNamed:@"LoadingView" owner:nil options:nil];
    _loadingView = [nib objectAtIndex:0];
    [self.tableView addSubview:_loadingView];
    [self activateTableView:NO];
    [self loadData];
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
    
    [[MintzatuAPIClient sharedClient] postPath:@"profile" parameters:params success:^(AFHTTPRequestOperation *operation, id responseObject) {
        NSDictionary *dict = [responseObject objectForKey:@"profile"];
        _friend = [[Friend alloc] initWithDictionary:dict];
        
        NSNumber *friend = [dict objectForKey:@"friends"];
        if (friend != nil) {
            _showFriendshipCell = YES;
            _showFriendRequestedCell = NO;
        } else {
            if ([_friend.friendshipState isEqualToString:@"0"]) {
                _showFriendshipCell = YES;
                _showFriendRequestedCell = YES;
            }
        }
        
        [self.tableView reloadData];
        [self.refreshControl endRefreshing];
        
        if (_loadingView != nil) {
            [UIView animateWithDuration:1.0f animations:^{
                _loadingView.alpha = 1.0f;
            } completion:^(BOOL finished) {
                [_loadingView removeFromSuperview];
                [self activateTableView:YES];
            }];
        } else {
            [self activateTableView:YES];
        }
        
    } failure:nil];
}

-(void)badgesClick
{
    [self performSegueWithIdentifier:@"badges" sender:nil];
}

- (void)mayorshipClick
{
    [self performSegueWithIdentifier:@"mayorships" sender:nil];
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    if ([segue.identifier isEqualToString:@"badges"]) {
        BadgesViewController *controller = (BadgesViewController*) segue.destinationViewController;
        controller.profileId = _profileId;
    } else {
        MayorshipsViewController *controller = (MayorshipsViewController*) segue.destinationViewController;
        controller.profileId = _profileId;
    }
}

#pragma mark TableView

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.section == 2) {
        MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
        hud.labelText = @"Eskaera bidaltzen";
        
        NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
        [params setValue:[MintzatuAPIClient userId] forKey:@"id"];
        [params setValue:[MintzatuAPIClient userToken] forKey:@"token"];
        [params setValue:[NSNumber numberWithInteger:_profileId] forKey:@"idProfile"];
        
        [[MintzatuAPIClient sharedClient] postPath:@"add-friend" parameters:params success:^(AFHTTPRequestOperation *operation, id responseObject) {
            [hud hide:YES];
            _showFriendshipCell = YES;
            _showFriendRequestedCell = YES;
            [self.tableView reloadData];
        } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
            [hud hide:YES];
        }];
    }
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    if (_showFriendshipCell)
        return 3;
    return 2;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if (section == 0)
        return 3;
    return 1;
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSUInteger section = indexPath.section;
    NSUInteger row = indexPath.row;
    
    if (section == 0) {
        if (row == 0 || row == 2) {
            UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"SeparatorCell"];
            if (cell == nil) {
                NSArray *nib = [[NSBundle mainBundle] loadNibNamed:@"SeparatorCellView" owner:self options:nil];
                cell = (UITableViewCell *)[nib objectAtIndex:0];
            }
            return cell;
        } else {
            ProfileDataCell *cell = (ProfileDataCell*) [tableView dequeueReusableCellWithIdentifier:@"ProfilaDataCell"];
            cell.izenaLabel.text = _friend.username;
            if (_friend.lastPlaceName != nil) {
                cell.azkenLekuaTextField.text = [NSString stringWithFormat:@"Azken lekua:\n%@", _friend.lastPlaceName];
            } else {
                cell.azkenLekuaTextField.text = @"Oraindik ez du check-inik egin";
            }
            cell.azkenLekuaTextField.contentInset = UIEdgeInsetsMake(-4,-4,0,0);
            [cell.photoImageView setImageWithURL:[NSURL URLWithString:_friend.img]];
            return cell;
        }
    } else if (section == 1) {
        AwardsCell *cell = (AwardsCell*) [tableView dequeueReusableCellWithIdentifier:@"AwardsCell"];
        if (cell == nil) {
            NSArray *nib = [[NSBundle mainBundle] loadNibNamed:@"AwardsCellView" owner:self options:nil];
            cell = (AwardsCell *)[nib objectAtIndex:0];
        }
        
        cell.badgeButton.numbeLabel.text = [_friend.badges stringValue];
        [cell.badgeButton addTarget:self action:@selector(badgesClick) forControlEvents:UIControlEventTouchUpInside];
        cell.mayorshipButton.numbeLabel.text = [_friend.mayorships stringValue];
        [cell.mayorshipButton addTarget:self action:@selector(mayorshipClick) forControlEvents:UIControlEventTouchUpInside];
        
        return cell;
    } else {
        SimpleCell *cell;
        if (_showFriendRequestedCell) {
            cell = [tableView dequeueReusableCellWithIdentifier:@"LagunEskaeraBidalitaCell"];
        } else {
            cell = [tableView dequeueReusableCellWithIdentifier:@"LagunEskaeraCell"];
        }
        
        cell.type = SimpleCellTypeSingle;
        cell.borderWidth = 1.0f;
        cell.borderColor = [UIColor backgroundBeige];
        
        return cell;
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSUInteger section = indexPath.section;
    NSUInteger row = indexPath.row;
    
    if (section == 0) {
        if (row == 0 || row == 2)
            return 4.0f;
        else
            return 100.0f;
    } else if (section == 1) {
        return 70.0f;
    }
    
    return 56.0f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    if (section != 0) {
        return 10.0f;
    }
    return 0;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section
{
    if (section > 1) {
        return 10.0f;
    }
    return 0;
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
