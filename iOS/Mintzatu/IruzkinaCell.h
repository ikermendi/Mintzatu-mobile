//
//  IruzkinaCell.h
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 06/09/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "SimpleCell.h"

@interface IruzkinaCell : SimpleCell
@property (weak, nonatomic) IBOutlet UIImageView *avatarImageView;
@property (weak, nonatomic) IBOutlet UILabel *iruzkinaLabel;
@property (weak, nonatomic) IBOutlet UILabel *timeLabel;
@property (weak, nonatomic) IBOutlet UILabel *nameLabel;
@end
