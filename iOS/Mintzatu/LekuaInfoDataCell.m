//
//  LekuaInfoDataCell.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 26/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "LekuaInfoDataCell.h"

@interface LekuaInfoDataCell ()

@end

@implementation LekuaInfoDataCell


- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

-(void)layoutSubviews
{
    if (self.frame.size.height > 110.0f) {
        CGSize textSize = [_cellText sizeWithFont:[UIFont systemFontOfSize:13.0f] constrainedToSize:CGSizeMake(300, INT_MAX)];
        if (textSize.height > 21.0f) {
            CGRect descriptionFrame = _lekuaDescription.frame;
            descriptionFrame.size.height = textSize.height;
            self.backgroundColor = [UIColor backgroundProfile];
            CGRect separatorFrame = _bottonSeparatorView.frame;
            separatorFrame.origin.y += textSize.height;
            _bottonSeparatorView.frame = separatorFrame;
            _lekuaDescription.frame = descriptionFrame;
        }
    } else {
        _lekuaDescription.frame = CGRectMake(9, 83, 300, 21);
        _lekuaDescription.lineBreakMode = NSLineBreakByTruncatingTail;
    }
}

@end
