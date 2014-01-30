//
//  CheckinCell.h
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 30/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "SimpleCell.h"

@interface ProfileLekuaCell : SimpleCell
@property (weak, nonatomic) IBOutlet UILabel *nameLabel;
@property (weak, nonatomic) IBOutlet UILabel *streetLabel;
@property (weak, nonatomic) IBOutlet UIImageView *categoryImageView;
@end
