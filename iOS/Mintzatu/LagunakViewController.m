//
//  LagunakViewController.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 03/09/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "LagunakViewController.h"

#import "LagunaCell.h"
#import "MintzatuAPIClient.h"
#import "Friend.h"
#import "UIImageView+WebCache.h"
#import "MintzatuTableView.h"
#import "LagunaProfileViewController.h"

@interface LagunakViewController ()
{
    UIView *_loadingView;
    NSArray *_friends;
}
@property (weak, nonatomic) IBOutlet MintzatuTableView *tableView;
@end

@implementation LagunakViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.screenName = @"LagunakViewController";
    ((MintzatuTableView*)(self.tableView)).emptyText = @"Ez dago lagunik erakusteko";
    
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

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    _friends = nil;
    [self loadData];
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    _loadingView = nil;
    _friends = nil;
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
    
    [[MintzatuAPIClient sharedClient] postPath:@"all-my-friends" parameters:params success:^(AFHTTPRequestOperation *operation, id responseObject) {
        _friends = [responseObject objectForKey:@"allFriends"];
        
        [self.tableView reloadData];
        
        [UIView animateWithDuration:1.0f animations:^{
            _loadingView.alpha = 1.0f;
        } completion:^(BOOL finished) {
            [_loadingView removeFromSuperview];
            _loadingView = nil;
            [self activateTableView:YES];
        }];
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        double delayInSeconds = 5.0;
        dispatch_time_t popTime = dispatch_time(DISPATCH_TIME_NOW, (int64_t)(delayInSeconds * NSEC_PER_SEC));
        dispatch_after(popTime, dispatch_get_main_queue(), ^(void){
            [self loadData];
        });
    }];
}

- (IBAction)addFriends:(id)sender
{
    [self.tabBarController setSelectedIndex:4];
}


#pragma mark Segue

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    NSIndexPath *indexPath = (NSIndexPath*)sender;
    LagunaProfileViewController *controller = (LagunaProfileViewController*) segue.destinationViewController;
    controller.profileId = [[[_friends objectAtIndex:indexPath.row] objectForKey:@"id"] integerValue];
}


#pragma mark - TableView

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [self performSegueWithIdentifier:@"profile" sender:indexPath];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return _friends.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    LagunaCell *cell = [tableView dequeueReusableCellWithIdentifier:@"LagunaCell"];
    
    cell.borderWidth = 1.0f;
    cell.borderColor = [UIColor backgroundBeige];
    
    if (indexPath.row == 0) {
        cell.type = SimpleCellTypeTop;
    } else if (indexPath.row == _friends.count) {
        cell.type = SimpleCellTypeBottom;
    } else {
        cell.type = SimpleCellTypeMiddle;
    }
    
    NSDictionary *friendDict = [_friends objectAtIndex:indexPath.row];
    cell.nameLabel.text = [friendDict objectForKey:@"name"];
    [cell.photoImageView setImageWithURL:[NSURL URLWithString:[friendDict objectForKey:@"userImage"]]];
    
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
