//
//  LekuaInfoPhotoCell.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 27/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "LekuaInfoPhotoCell.h"

#import "UIImageView+AFNetworking.h"

@implementation LekuaInfoPhotoCell

- (void)setImagesWithPictures:(NSArray*)pictures
{
    NSUInteger i = 0;
    NSInteger count = pictures.count;
    for (UIView *view in self.containerView.subviews) {
        if (view.tag != 1 && [view isKindOfClass:[UIImageView class]]) {
            UIImageView *imageView = (UIImageView*)view;
            imageView.layer.borderColor = [[UIColor backgroundBeigeLight] CGColor];
            imageView.layer.borderWidth = 1.0f;
            
            if (count > 0) {
                NSDictionary *dict = [pictures objectAtIndex:i];
                NSURL *url = [NSURL URLWithString:[dict objectForKey:@"tinyImg"]];
                [imageView setImageWithURL:url placeholderImage:[UIImage imageNamed:@"PlaceHolder"]];
                imageView.hidden = NO;
                i++;
                count--;
            } else {
                imageView.hidden = YES;
            }
        }
    }
}

@end
