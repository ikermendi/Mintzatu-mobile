//
//  CheckinViewController.h
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 09/09/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "GAITrackedViewController.h"

@class Place;
@class Badge;

@protocol CheckinViewControllerDelegate <NSObject>
@required
- (void)reloadViewWithBadge:(Badge*)badge;
@end

@interface CheckinViewController : GAITrackedViewController
@property (nonatomic) Place *place;
@property (nonatomic) id<CheckinViewControllerDelegate> delegate;
@end
