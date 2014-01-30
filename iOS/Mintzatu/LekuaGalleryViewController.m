//
//  LekuaGalleryViewController.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 28/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "LekuaGalleryViewController.h"

#import "LekuaGalleryDataSource.h"
#import "GAITrackedViewController.h"
#import "GAI.h"
#import "GAIDictionaryBuilder.h"
#import "GAIFields.h"

@interface LekuaGalleryViewController ()
{
    LekuaGalleryDataSource *_imagesDataSource;
}
@end

@implementation LekuaGalleryViewController


- (void)viewDidLoad
{
    [super viewDidLoad];
    id tracker = [GAI sharedInstance].defaultTracker;
    [tracker set:kGAIScreenName value:@"LekuakGalleryViewController"];
    [tracker send:[[GAIDictionaryBuilder createAppView]  build]];
    self.view.backgroundColor = [UIColor backgroundBeigeLight];    
    _imagesDataSource = [[LekuaGalleryDataSource alloc] initWithCompletitonBlock:^(BOOL success) {
        if (success) {
            [self setDataSource:_imagesDataSource];
        } else {
            NSString *_emptyText = @"Ez dago irudirik";
            UIFont *font = [UIFont fontWithName:@"Helvetica-Bold" size:17.0f];
            CGSize size = [_emptyText sizeWithFont:font constrainedToSize:CGSizeMake(self.view.frame.size.width-40, self.view.frame.size.height-40)];
            UILabel *_emptyLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 20,  self.view.frame.size.width, size.height)];
            _emptyLabel.textColor = [UIColor darkGrayColor];
            _emptyLabel.font = font;
            _emptyLabel.text = _emptyText;
            _emptyLabel.textAlignment = NSTextAlignmentCenter;
            _emptyLabel.numberOfLines = 0;
            _emptyLabel.backgroundColor = [UIColor clearColor];
            [self.view addSubview:_emptyLabel];
        }
    } placeId:_placeId];
    self.showDetailView = YES;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
