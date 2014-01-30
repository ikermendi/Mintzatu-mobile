//
//  LekuaCell.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 20/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "LekuaCell.h"

@implementation LekuaCell

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];
    if (selected) {
        self.containerView.backgroundColor = [UIColor mintzatuSelectedBlue];
    } else {
        self.containerView.backgroundColor = [UIColor whiteColor];
    }
}

@end
