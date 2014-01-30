//
//  LekuaViewController.h
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 21/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "GAITrackedViewController.h"

@class Place;

@interface LekuaInfoViewController : UITableViewController
@property (nonatomic, strong) Place *place;
@end
