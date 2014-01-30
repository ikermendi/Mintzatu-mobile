//
//  FriendRequestViewController.h
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 02/09/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "GAITrackedViewController.h"

@class FriendRequestCell;

@interface FriendRequestViewController : UITableViewController
@property (nonatomic) NSInteger profileId;
- (void)deleteRequestWithCell:(FriendRequestCell*)cell;
@end
