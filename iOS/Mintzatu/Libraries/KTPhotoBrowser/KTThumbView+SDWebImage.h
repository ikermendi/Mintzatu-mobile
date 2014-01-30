//
//  KTThumbView+SDWebImage.h
//  Sample
//
//  Created by Henrik Nyh on 3/18/10.
//

#import "KTThumbView.h"
#import "SDWebImageManager.h"

@interface  KTThumbView (SDWebImage)

- (void)setImageWithURL:(NSURL *)url;
- (void)setImageWithURL:(NSURL *)url placeholderImage:(UIImage *)placeholder;

@end
