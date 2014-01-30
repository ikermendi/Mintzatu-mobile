//
//  LekuaInfoPhotoCell.h
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 27/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "SimpleCell.h"

@interface LekuaInfoPhotoCell : SimpleCell
@property (weak, nonatomic) IBOutlet UIView *containerView;
- (void)setImagesWithPictures:(NSArray*)pictures;
@end
