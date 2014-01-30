//
//  CategoryCell.h
//  Mintzatu
//
//  Created by Sergio Garcia on 28/11/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "SimpleCell.h"

@interface CategoryCell : SimpleCell
@property (weak, nonatomic) IBOutlet UIImageView *imgCategory;
@property (weak, nonatomic) IBOutlet UILabel *lblCategory;
@property (weak, nonatomic) IBOutlet UIView *containerView;

@end
