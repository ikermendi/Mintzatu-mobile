//
//  ProfilaViewController.h
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 19/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "GAITrackedViewController.h"

extern NSString* const FriendRequestNotification;

@class Me;

@interface ProfilaViewController : UITableViewController
@property (nonatomic) Me *me;
@end
