//
//  TwitterAcountsViewController.h
//  Mintzatu
//
//  Created by Sergio Garcia on 19/12/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "SocialEzarpenakCell.h"
#import "SocialAccountWrapper.h"

@interface TwitterAcountsViewController : UIViewController

@property (strong, nonatomic) SocialEzarpenakCell *twCell;
@property (strong, nonatomic) NSMutableArray *twArrayAcounts;
@property (strong, nonatomic) SocialAccountWrapper *socialWrapper;
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (weak, nonatomic) IBOutlet UITableView *topSeparator;

@end
