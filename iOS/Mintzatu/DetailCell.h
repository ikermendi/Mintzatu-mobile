//
//  DetailCell.h
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 26/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "SimpleCell.h"

@interface DetailCell: SimpleCell
@property (weak, nonatomic) IBOutlet UILabel *infoLabel;
@property (weak, nonatomic) IBOutlet UITextField *detailTextField;

@end
