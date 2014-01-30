//
//  LekuaInfoDataCell.h
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 26/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface LekuaInfoDataCell : UITableViewCell
@property (strong, nonatomic) NSString *cellText;
@property (assign, nonatomic) BOOL detailedDescription;
@property (weak, nonatomic) IBOutlet UIImageView *lekuaImageView;
@property (weak, nonatomic) IBOutlet UILabel *lekuaName;
@property (weak, nonatomic) IBOutlet UITextView *lekuaStreet;
@property (weak, nonatomic) IBOutlet UILabel *lekuaDescription;
@property (weak, nonatomic) IBOutlet UIView *bottonSeparatorView;
@end
