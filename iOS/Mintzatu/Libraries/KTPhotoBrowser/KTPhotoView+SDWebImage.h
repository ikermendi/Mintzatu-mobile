//
//  KTPhotoView+SDWebImage.h
//  Sample
//
//  Created by Henrik Nyh on 3/18/10.
//

#import "KTPhotoView.h"
#import "SDWebImageManager.h"

@interface KTPhotoView (SDWebImage)

- (void)setImageWithURL:(NSURL *)url;
- (void)setImageWithURL:(NSURL *)url placeholderImage:(UIImage *)placeholder;

@end