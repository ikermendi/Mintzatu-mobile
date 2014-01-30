//
//  AwardButton.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 29/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "AwardButton.h"

@interface AwardButton ()
{
    UIView *_selectedView;
}
@end

@implementation AwardButton

- (id)initWithCoder:(NSCoder *)aDecoder
{
    self = [super initWithCoder:aDecoder];
    if (self) {
        //[self addTarget:self action:@selector(onClickDown) forControlEvents:UIControlEventTouchDown];
        //[self addTarget:self action:@selector(onClickUp) forControlEvents:UIControlEventTouchUpInside];
        CGPathRef path = [UIBezierPath bezierPathWithRect:self.bounds].CGPath;
        self.layer.shadowPath = path;
        self.layer.shadowColor = [UIColor darkGrayColor].CGColor;
        self.layer.shadowOpacity = 0.3f;
        self.layer.shadowOffset = CGSizeMake(1.0f, 1.0f);
        self.layer.shadowRadius = 1.0f;
        self.layer.masksToBounds = NO;

    }
    return self;
}

- (void)onClickDown
{
    if (_selectedView == nil) {
        _selectedView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.frame.size.width, self.frame.size.height)];
        _selectedView.backgroundColor = [UIColor mintzatuBlue];
        _selectedView.alpha = 0.75f;
    }
    [self addSubview:_selectedView];
}

- (void)onClickUp
{
    [_selectedView removeFromSuperview];
}

- (void)setNumber:(NSString *)number
{
    self.number = number;
    
}

- (void)drawRect:(CGRect)rect
{
    [super drawRect:rect];
    
    //self.layer.borderColor = [UIColor backgroundBeige].CGColor;
    //self.layer.borderWidth = 1.0f;
}


@end
