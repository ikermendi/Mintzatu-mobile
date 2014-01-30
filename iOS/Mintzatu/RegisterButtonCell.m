//
//  RegisterButtonCell.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 04/10/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "RegisterButtonCell.h"

@implementation RegisterButtonCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        
    }
    return self;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    [self.registerButton setButtonBackgroundColor:[UIColor mintzatuBlue]];
    [self.registerButton setButtonForegroundColor:[UIColor mintzatuBlue]];
    [self.registerButton setTitle:@"Erregistratu" forState:UIControlStateNormal];
    [self.registerButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.registerButton setTitleColor:[UIColor whiteColor] forState:UIControlStateHighlighted];
}

@end
