//
//  CategoryCell.m
//  Mintzatu
//
//  Created by Sergio Garcia on 28/11/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "CategoryCell.h"

@implementation CategoryCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        // Initialization code
    }
    return self;
}

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
