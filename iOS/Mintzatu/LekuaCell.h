//
//  LekuaCell.h
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 20/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "SimpleCell.h"

@interface LekuaCell : SimpleCell
@property (weak, nonatomic) IBOutlet UILabel *nameLabel;
@property (weak, nonatomic) IBOutlet UILabel *streetLabel;
@property (weak, nonatomic) IBOutlet UIImageView *categoryImageView;
@property (weak, nonatomic) IBOutlet UIView *containerView;
@end
