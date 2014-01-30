//
//  ZoomImageViewcontrollerViewController.h
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 10/09/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "GAITrackedViewController.h"

@interface UIImageView (Expand)
- (void)setup;
@end

@interface ZoomImageViewController : GAITrackedViewController
@property (weak, readonly, nonatomic) UIViewController *rootViewController;
@property (nonatomic,strong) UIImageView * senderView;
- (void)presentFromRootViewController;
@end
