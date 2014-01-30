//
//  BadgeCell.h
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 29/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "SimpleCell.h"

@class AwardButton;

@interface AwardsCell : SimpleCell
@property (weak, nonatomic) IBOutlet AwardButton *badgeButton;
@property (weak, nonatomic) IBOutlet AwardButton *mayorshipButton;

@end
