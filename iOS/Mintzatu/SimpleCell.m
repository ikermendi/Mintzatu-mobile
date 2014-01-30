//
//  LekuaInfoSimpleCell.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 28/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//


#import "SimpleCell.h"

@implementation ContainerView

+(Class) layerClass {
    return [AUISelectiveBordersLayer class];
}

-(AUISelectiveBordersFlag) selectiveBorderFlag
{
    AUISelectiveBordersLayer *selectiveLayer = (AUISelectiveBordersLayer *)self.layer;
    return selectiveLayer.selectiveBorderFlag;
}

-(void) setSelectiveBorderFlag:(AUISelectiveBordersFlag)selectiveBorderFlag
{
    AUISelectiveBordersLayer *selectiveLayer = (AUISelectiveBordersLayer *)self.layer;
    selectiveLayer.selectiveBorderFlag = selectiveBorderFlag;
}

-(UIColor *)selectiveBordersColor
{
    AUISelectiveBordersLayer *selectiveLayer = (AUISelectiveBordersLayer *)self.layer;
    return selectiveLayer.selectiveBordersColor;
}

-(void) setSelectiveBordersColor:(UIColor *)selectiveBordersColor
{
    AUISelectiveBordersLayer *selectiveLayer = (AUISelectiveBordersLayer *)self.layer;
    selectiveLayer.selectiveBordersColor = selectiveBordersColor;
}

-(float) selectiveBordersWidth
{
    AUISelectiveBordersLayer *selectiveLayer = (AUISelectiveBordersLayer *)self.layer;
    return selectiveLayer.selectiveBordersWidth;
}

-(void) setSelectiveBordersWidth:(float)selectiveBordersWidth
{
    AUISelectiveBordersLayer *selectiveLayer = (AUISelectiveBordersLayer *)self.layer;
    selectiveLayer.selectiveBordersWidth = selectiveBordersWidth;
}

@end

@implementation SimpleCell

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    if (selected) {
        self.containerView.backgroundColor = [UIColor mintzatuSelectedBlue];
    } else {
        self.containerView.backgroundColor = [UIColor whiteColor];
    }
}

- (void)setType:(SimpleCellType)type
{
    if (type == SimpleCellTypeTop || type ==  SimpleCellTypeSingle ) {
        self.containerView.selectiveBorderFlag = AUISelectiveBordersFlagLeft|AUISelectiveBordersFlagTop|AUISelectiveBordersFlagRight|AUISelectiveBordersFlagBottom;
    } else if (type ==  SimpleCellTypeBottom) {
        self.containerView.selectiveBorderFlag = AUISelectiveBordersFlagLeft|AUISelectiveBordersFlagBottom|AUISelectiveBordersFlagRight;
    } else if (type ==  SimpleCellTypeMiddle) {
        self.containerView.selectiveBorderFlag = AUISelectiveBordersFlagLeft|AUISelectiveBordersFlagRight|AUISelectiveBordersFlagBottom;
    }
}

- (void)setBorderColor:(UIColor *)borderColor
{
    self.containerView.selectiveBordersColor = borderColor;
}

- (void)setBorderWidth:(CGFloat)borderWidth
{
    self.containerView.selectiveBordersWidth = borderWidth;
}

@end
