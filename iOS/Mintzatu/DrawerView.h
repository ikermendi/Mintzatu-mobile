//
//  DrawerView.h
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 03/09/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol DrawerDelegate <NSObject>
@required
- (void)newRect:(CGRect)rect;
- (UIView *)viewToMove;
@end

@interface DrawerView : UIView
@property (nonatomic) CGFloat maxY, minY;
@property (weak, nonatomic) id<DrawerDelegate> delegate;
@end
