//
//  LekuaInfoGenericCell.h
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 04/09/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "SimpleCell.h"
#import "Activity.h"

@interface LekuaInfoGenericCell : SimpleCell
@property (weak, nonatomic) IBOutlet UIImageView *userImageView;
@property (weak, nonatomic) IBOutlet UILabel *whoLabel;
@property (weak, nonatomic) IBOutlet UILabel *timeTypeLabel;
@property (nonatomic) Activity* activity;
@end
