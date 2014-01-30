//
//  FriendRequestViewController.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 02/09/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "FriendRequestViewController.h"

#import "FriendRequestCell.h"
#import "FriendRequest.h"
#import "MintzatuAPIClient.h"
#import "UIImageView+WebCache.h"
#import "MintzatuTableView.h"
#import "GAITrackedViewController.h"
#import "GAI.h"
#import "GAIDictionaryBuilder.h"
#import "GAIFields.h"

@interface FriendRequestViewController ()
{
    UIView *_loadingView;
    NSMutableArray *_requests;
}
@end

@implementation FriendRequestViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    id tracker = [GAI sharedInstance].defaultTracker;
    [tracker set:kGAIScreenName value:@"FriendRequestViewController"];
    [tracker send:[[GAIDictionaryBuilder createAppView]  build]];
    _requests = [[NSMutableArray alloc] init];
    [self loadData];
}


- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
}

- (void)dealloc
{
    _loadingView = nil;
    _requests = nil;
}

- (void)activateTableView:(BOOL)activate
{
    //Quitamos el scroll para que al cargar no se muestren
    self.tableView.scrollEnabled = activate;
    self.tableView.userInteractionEnabled = activate;
}

- (void)loadData
{
    [self activateTableView:NO];
    
    ((MintzatuTableView*)(self.tableView)).emptyText = @"Ez dago lagun eskaerarik erakusteko";
    
    NSArray *nib = [[NSBundle mainBundle] loadNibNamed:@"LoadingView" owner:nil options:nil];
    _loadingView = [nib objectAtIndex:0];
    [self.tableView addSubview:_loadingView];
    
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    [params setValue:[MintzatuAPIClient userId] forKey:@"id"];
    [params setValue:[MintzatuAPIClient userToken] forKey:@"token"];
    [params setValue:[NSNumber numberWithInteger:_profileId] forKey:@"idProfile"];
    
    [[MintzatuAPIClient sharedClient] postPath:@"get-friend-requests" parameters:params success:^(AFHTTPRequestOperation *operation, id responseObject) {
        NSDictionary *dict = [responseObject objectForKey:@"requests"];
        
        for (NSDictionary *friendDict in dict) {
            FriendRequest *request = [[FriendRequest alloc] initWithDictionary:friendDict];
            [_requests addObject:request];
        }
        
        [self.tableView reloadData];
        
        [UIView animateWithDuration:1.0f animations:^{
            _loadingView.alpha = 1.0f;
        } completion:^(BOOL finished) {
            [_loadingView removeFromSuperview];
            _loadingView = nil;
            [self activateTableView:YES];
        }];
    } failure:nil];
}

- (void)deleteRequestWithCell:(FriendRequestCell*)cell
{
    [_requests removeObject:cell.requestFriend];
    [self.tableView deleteRowsAtIndexPaths:@[[self.tableView indexPathForCell:cell]] withRowAnimation:UITableViewRowAnimationAutomatic];
}

#pragma mark - TableView

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return _requests.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    FriendRequestCell *cell = [tableView dequeueReusableCellWithIdentifier:@"FriendRequestCell"];
    
    cell.borderWidth = 1.0f;
    cell.borderColor = [UIColor backgroundBeige];
    
    if (indexPath.row == 0) {
        cell.type = SimpleCellTypeTop;
    } else if (indexPath.row == _requests.count) {
        cell.type = SimpleCellTypeBottom;
    } else {
        cell.type = SimpleCellTypeMiddle;
    }
    
    FriendRequest *request = [_requests objectAtIndex:indexPath.row];
    cell.requestFriend = request;
    cell.controller = self;
    [cell.friendImageView setImageWithURL:[NSURL URLWithString:request.userImg]];
    
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
    return 90.0f;
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
