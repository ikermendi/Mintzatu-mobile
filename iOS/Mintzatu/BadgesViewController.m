//
//  BadgesViewController.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 30/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "BadgesViewController.h"

#import "UIImageView+WebCache.h"
#import "MintzatuAPIClient.h"
#import "BadgeCell.h"
#import "Badge.h"
#import "MintzatuCollectionView.h"

@interface BadgesViewController () <UICollectionViewDataSource, UICollectionViewDelegate>
{
    UIView *_loadingView;
    NSMutableArray *_badges;
}
@property (weak, nonatomic) IBOutlet MintzatuCollectionView *collectionView;
@end

@implementation BadgesViewController


- (void)viewDidLoad
{
    [super viewDidLoad];
    self.screenName = @"BadgesViewController";
    _badges = [[NSMutableArray alloc] init];
    
    ((MintzatuCollectionView*)(self.collectionView)).emptyText = @"Ez dago dominarik erakusteko";
	
    NSArray *nib = [[NSBundle mainBundle] loadNibNamed:@"LoadingView" owner:nil options:nil];
    _loadingView = [nib objectAtIndex:0];
    [self.collectionView addSubview:_loadingView];
    
    [self loadData];
}


- (void)loadData
{
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    [params setValue:[MintzatuAPIClient userId] forKey:@"id"];
    [params setValue:[MintzatuAPIClient userToken] forKey:@"token"];
    [params setValue:[NSNumber numberWithInteger:_profileId] forKey:@"idProfile"];
    [[MintzatuAPIClient sharedClient] postPath:@"get-badges" parameters:params success:^(AFHTTPRequestOperation *operation, id responseObject) {
        NSArray *badgesArray = [responseObject objectForKey:@"badges"];
        if (![badgesArray isKindOfClass:[NSNull class]]) {
            for (NSDictionary *badgeDict in badgesArray) {
                Badge *badge = [[Badge alloc] initWithDictionary:badgeDict];
                [_badges addObject:badge];
            }
            [self.collectionView reloadData];
        }
        [_loadingView removeFromSuperview];
    } failure:nil];
}

- (void)dealloc
{
    _loadingView = nil;
    _badges = nil;
}

#pragma mark CollectionView

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
{
    return _badges.count;
}


- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    BadgeCell *cell = (BadgeCell*) [collectionView dequeueReusableCellWithReuseIdentifier:@"BadgeCell" forIndexPath:indexPath];
    Badge *badge = [_badges objectAtIndex:indexPath.row];
    cell.badgeName.text = badge.name;
    [cell.badgePhoto setImageWithURL:[NSURL URLWithString:badge.img]];
    cell.layer.borderColor = [[UIColor backgroundBeige] CGColor];
    cell.layer.borderWidth = 1.0f;
    return cell;
}

@end
