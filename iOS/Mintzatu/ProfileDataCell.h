//
//  ProfileDataCell.h
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 29/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ProfileDataCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UILabel *izenaLabel;
@property (weak, nonatomic) IBOutlet UITextView *azkenLekuaTextField;
@property (weak, nonatomic) IBOutlet UIImageView *photoImageView;
@end
