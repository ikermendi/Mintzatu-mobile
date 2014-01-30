//
//  KTThumbView+SDWebImage.m
//  Sample
//
//  Created by Henrik Nyh on 3/18/10.
//

#import "KTThumbView+SDWebImage.h"
#import "SDWebImageManager.h"

@implementation KTThumbView (SDWebImage)

- (void)setImageWithURL:(NSURL *)url {
   [self setImageWithURL:url placeholderImage:nil];
}

- (void)setImageWithURL:(NSURL *)url placeholderImage:(UIImage *)placeholder {
   SDWebImageManager *manager = [SDWebImageManager sharedManager];
    
    [manager downloadWithURL:url options:0 progress:nil completed:^(UIImage *image, NSError *error, SDImageCacheType cacheType, BOOL finished) {
         [self setThumbImage:image];
    }];
}


@end
