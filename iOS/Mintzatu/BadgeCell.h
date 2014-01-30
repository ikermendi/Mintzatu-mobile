//
//  BadgeCell.h
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 02/09/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface BadgeCell : UICollectionViewCell
@property (weak, nonatomic) IBOutlet UIImageView *badgePhoto;
@property (weak, nonatomic) IBOutlet UILabel *badgeName;

@end
