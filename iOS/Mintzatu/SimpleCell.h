//
//  LekuaInfoSimpleCell.h
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 28/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "AUISelectiveBordersLayer.h"

enum {
    SimpleCellTypeTop,
    SimpleCellTypeBottom,
    SimpleCellTypeMiddle,
    SimpleCellTypeSingle,
};
typedef NSUInteger SimpleCellType;

@interface ContainerView : UIView

@property (nonatomic, strong) UIColor *selectiveBordersColor;
@property (nonatomic) float selectiveBordersWidth;
@property (nonatomic) AUISelectiveBordersFlag selectiveBorderFlag;

@end

@interface SimpleCell : UITableViewCell
@property (strong, nonatomic) IBOutlet ContainerView *containerView;
@property (weak, nonatomic) IBOutlet UILabel *cellTextLabel;
@property (nonatomic) CGFloat borderWidth;
@property (nonatomic) UIColor *borderColor;
@property (nonatomic) SimpleCellType type;
@end
