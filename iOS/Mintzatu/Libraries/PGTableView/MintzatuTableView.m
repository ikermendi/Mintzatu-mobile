//
//  MintzatuTableView.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 03/09/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "MintzatuTableView.h"

#import <QuartzCore/QuartzCore.h>

@interface MintzatuTableView ()
{
    UIView *_emptyView;
    UILabel *_emptyLabel;
}
@end

@implementation MintzatuTableView


- (id)initWithCoder:(NSCoder *)aDecoder
{
    self = [super initWithCoder:aDecoder];
    if (self) {
        [self initialize];
    }
    return self;
}

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self initialize];
    }
    return self;
}

- (id)initWithFrame:(CGRect)frame style:(UITableViewStyle)style
{
    self = [super initWithFrame:frame style:style];
    if (self) {
        [self initialize];
    }
    return self;
}

- (void)initialize
{
    _emptyView = [[UIView alloc] initWithFrame:CGRectMake(20, 20, self.frame.size.width-40, self.frame.size.height-40)];
    [self addSubview:_emptyView];
    [self setEmptyText:@"Zerranda hutsik dago"];
}

- (void)setEmptyText:(NSString *)emptyText
{
    _emptyText = emptyText;
    if (_emptyLabel != nil) {
        [_emptyLabel removeFromSuperview];
    }
    UIFont *font = [UIFont fontWithName:@"Helvetica-Bold" size:17.0f];
    CGSize size = [_emptyText sizeWithFont:font constrainedToSize:CGSizeMake(self.frame.size.width-40, self.frame.size.height-40)];
    _emptyLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0,  self.frame.size.width-40, size.height)];
    _emptyLabel.textColor = [UIColor darkGrayColor];
    _emptyLabel.font = font;
    _emptyLabel.text = _emptyText;
    _emptyLabel.textAlignment = NSTextAlignmentCenter;
    _emptyLabel.numberOfLines = 0;
    _emptyLabel.backgroundColor = [UIColor clearColor];
    [_emptyView addSubview:_emptyLabel];
}

- (bool)tableViewHasRows
{
    return [self numberOfRowsInSection:0] == 0;
}

- (void)updateEmptyPage
{
    const bool shouldShowEmptyView = self.tableViewHasRows;
    const bool emptyViewShown      = _emptyView.superview != nil;
    
    if (shouldShowEmptyView == emptyViewShown) return;
    
    CATransition *animation = [CATransition animation];
    [animation setDuration:0.5];
    [animation setType:kCATransitionFade];
    [animation setTimingFunction:[CAMediaTimingFunction functionWithName:kCAMediaTimingFunctionEaseIn]];
    [[self layer] addAnimation:animation forKey:kCATransitionReveal];
    
    if (shouldShowEmptyView) {
        [self addSubview:_emptyView];
    } else {
        [_emptyView removeFromSuperview];
    }
}

#pragma mark UIView

- (void) layoutSubviews
{
    [super layoutSubviews];
    [self updateEmptyPage];
}

- (UIView*) hitTest:(CGPoint)point withEvent:(UIEvent *)event
{
    // Prevent any interaction when the empty view is shown
    const bool emptyViewShown = _emptyView.superview != nil;
    return emptyViewShown ? nil : [super hitTest:point withEvent:event];
}

#pragma mark UITableView

- (void) reloadData
{
    [super reloadData];
    [self updateEmptyPage];
}


@end