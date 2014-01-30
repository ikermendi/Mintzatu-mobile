//
//  BadgeCell.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 29/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "AwardsCell.h"

#import "AwardButton.h"

@implementation AwardsCell

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

    // Configure the view for the selected state
}

@end
