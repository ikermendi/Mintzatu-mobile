//
//  LekuaGalleryDataSource.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 09/09/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "LekuaGalleryDataSource.h"

#import "MintzatuAPIClient.h"
#import "KTPhotoView+SDWebImage.h"
#import "KTThumbView+SDWebImage.h"

#define FULL_SIZE_INDEX 0
#define THUMBNAIL_INDEX 1

@interface LekuaGalleryDataSource ()
{
    NSMutableArray *_images;
    NSUInteger _page;
    BOOL _finishLoading;
    NSNumber *_placeId;
}
@property (copy) void (^block) (BOOL success);
@end

@implementation LekuaGalleryDataSource

- (id)initWithCompletitonBlock:(void (^)(BOOL success))block placeId:(NSNumber*)placeId
{
    self = [super init];
    if (self) {
        _images = [[NSMutableArray alloc] init];
        _page = 1;
        _placeId = placeId;
        self.block = block;
        [self loadData];
    }
    return self;
}

- (void)dealloc
{
    _images = nil;
    _placeId = nil;
    self.block = nil;
}

- (void)loadData
{
    if (_finishLoading == NO) {
        NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
        [params setValue:[MintzatuAPIClient userId] forKey:@"id"];
        [params setValue:[MintzatuAPIClient userToken] forKey:@"token"];
        [params setValue:_placeId forKey:@"idPlace"];
        [params setValue:[NSString stringWithFormat:@"%d", _page] forKey:@"page"];
        [params setValue:@"20" forKey:@"items"];
        
        [[MintzatuAPIClient sharedClient] postPath:@"get-place-pictures" parameters:params success:^(AFHTTPRequestOperation *operation, id responseObject) {
            // Create a 2-dimensional array. First element of
            // the sub-array is the full size image URL and
            // the second element is the thumbnail URL.
            NSArray *picturesData = [responseObject objectForKey:@"pictures"];
            if ([picturesData isEqual:[NSNull null]]) {
                self.block(NO);
                return;
            }
            for (int i = 0; i < 20 && i < picturesData.count; i++) {
                NSDictionary *pictureDict = [picturesData objectAtIndex:i];
                NSMutableDictionary *pictureData = [[NSMutableDictionary alloc] init];
                [pictureData setValue:[pictureDict objectForKey:@"username"] forKey:@"username"];
                [pictureData setValue:[pictureDict objectForKey:@"datetime"] forKey:@"date"];
                NSArray *array = [NSArray arrayWithObjects:[pictureDict objectForKey:@"normalImg"], [pictureDict objectForKey:@"normalImg"], pictureData, nil];
                [_images addObject:array];
            }
            
            if (picturesData.count <= 20)
                _finishLoading = YES;
            
            _page++;
            
            self.block(YES);
        } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
            self.block(NO);
        }];
    }
}

- (void)loadMoreElements:(void (^)(BOOL success))completition
{
    self.block = completition;
    [self loadData];
}

- (NSInteger)numberOfPhotos
{
    NSInteger count = [_images count];
    return count;
}

- (void)imageAtIndex:(NSInteger)index photoView:(KTPhotoView *)photoView {
    NSArray *imageUrls = [_images objectAtIndex:index];
    NSString *url = [imageUrls objectAtIndex:FULL_SIZE_INDEX];
    [photoView setImageWithURL:[NSURL URLWithString:url] placeholderImage:[UIImage imageNamed:@"Placeholder"]];
}

- (void)thumbImageAtIndex:(NSInteger)index thumbView:(KTThumbView *)thumbView {
    NSArray *imageUrls = [_images objectAtIndex:index];
    NSString *url = [imageUrls objectAtIndex:THUMBNAIL_INDEX];
    [thumbView setImageWithURL:[NSURL URLWithString:url] placeholderImage:[UIImage imageNamed:@"Placeholder"]];
}

- (NSDictionary*)getPhotoDescriptionAtIndex:(NSInteger)index
{
    NSDictionary *pictureData = [[_images objectAtIndex:index] objectAtIndex:2];
    return pictureData;
}

@end
