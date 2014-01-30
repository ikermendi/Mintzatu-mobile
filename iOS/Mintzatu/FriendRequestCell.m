//
//  FriendRequestCell.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 02/09/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "FriendRequestCell.h"

#import <CoreText/CoreText.h>
#import "MintzatuAPIClient.h"
#import "MBProgressHUD.h"
#import "JSFlatButton.h"
#import "FriendRequest.h"
#import "FriendRequestViewController.h"
#import "ProfilaViewController.h"

@interface FriendContainerView : ContainerView
@property (nonatomic) NSString *friendName;
@end

@implementation FriendContainerView

- (void)drawRect:(CGRect)rect
{
    [super drawRect:rect];
    
	NSString *suffix = @"-(e)k zure laguna izan nahi du";
    NSString *longText = [NSString stringWithFormat:@"%@%@", _friendName, suffix];
	NSMutableAttributedString *string = [[NSMutableAttributedString alloc] initWithString:longText];
    
	// make a few words bold
	CTFontRef helvetica = CTFontCreateWithName(CFSTR("Helvetica"), 14.0, NULL);
	CTFontRef helveticaBold = CTFontCreateWithName(CFSTR("Helvetica-Bold"), 14.0, NULL);
    

    
	[string addAttribute:(id)kCTFontAttributeName
                   value:(__bridge id)helveticaBold
                   range:NSMakeRange(0, [_friendName length])];
    
    [string addAttribute:(id)kCTFontAttributeName
                   value:(__bridge id)helvetica
                   range:NSMakeRange([_friendName length], [suffix length])];
    
    
	// add some color
	[string addAttribute:(id)kCTForegroundColorAttributeName
                   value:(id)[UIColor mintzatuBlue].CGColor
                   range:NSMakeRange(0, [_friendName length])];
    
	[string addAttribute:(id)kCTForegroundColorAttributeName
                   value:(id)[UIColor darkGrayColor].CGColor
                   range:NSMakeRange([_friendName length], [suffix length])];
    
	// layout master
	CTFramesetterRef framesetter = CTFramesetterCreateWithAttributedString((CFAttributedStringRef)string);
    
	// left column form
	CGMutablePathRef leftColumnPath = CGPathCreateMutable();
	CGPathAddRect(leftColumnPath, NULL, CGRectMake(80, self.bounds.size.height - 50 - 6 /*50px=Self height, 6px=Top padding*/, 200, 50));
    
	// left column frame
	CTFrameRef leftFrame = CTFramesetterCreateFrame(framesetter, CFRangeMake(0, 0), leftColumnPath, NULL);
    
    
	// flip the coordinate system
	CGContextRef context = UIGraphicsGetCurrentContext();
	CGContextSetTextMatrix(context, CGAffineTransformIdentity);
	CGContextTranslateCTM(context, 0, self.bounds.size.height);
	CGContextScaleCTM(context, 1.0, -1.0);
    
	// draw
	CTFrameDraw(leftFrame, context);
    
	// cleanup
	CFRelease(leftFrame);
	CGPathRelease(leftColumnPath);
	CFRelease(framesetter);
	CFRelease(helvetica);
	CFRelease(helveticaBold);
}

@end

@implementation FriendRequestCell

-(void)awakeFromNib
{
    [self.onartuButton setButtonBackgroundColor:[UIColor mintzatuBlue]];
    [self.onartuButton setButtonForegroundColor:[UIColor mintzatuBlue]];
    [self.onartuButton setTitle:@"Onartu" forState:UIControlStateNormal];
    [self.onartuButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.onartuButton setTitleColor:[UIColor whiteColor] forState:UIControlStateHighlighted];
    [self.onartuButton addTarget:self action:@selector(onartuClicked) forControlEvents:UIControlEventTouchUpInside];
    
    [self.ukatuButton setButtonBackgroundColor:[UIColor buttonBeige]];
    [self.ukatuButton setButtonForegroundColor:[UIColor buttonBeige]];
    [self.ukatuButton setShouldHighlightImage:YES];
    [self.ukatuButton setTitle:@"Ukatu" forState:UIControlStateNormal];
    [self.ukatuButton setTitleColor:[UIColor darkGrayColor] forState:UIControlStateNormal];
    [self.ukatuButton setTitleColor:[UIColor darkGrayColor] forState:UIControlStateHighlighted];
    [self.ukatuButton addTarget:self action:@selector(ukatuClicked) forControlEvents:UIControlEventTouchUpInside];
}

- (void)onartuClicked
{
    [self sendPostWithAnswer:1 dialogText:@"Eskaera onartzen"];
}

- (void)ukatuClicked
{
    [self sendPostWithAnswer:2 dialogText:@"Eskaera ukatzen"];
}

- (void)sendPostWithAnswer:(NSUInteger)answer dialogText:(NSString*)text
{
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    [params setValue:[MintzatuAPIClient userId] forKey:@"id"];
    [params setValue:[MintzatuAPIClient userToken] forKey:@"token"];
    [params setValue:[MintzatuAPIClient userId] forKey:@"idProfile"];
    [params setValue:_requestFriend.idRel forKey:@"idRel"];
    [params setValue:[NSNumber numberWithInt:answer] forKey:@"answer"];
    
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:_controller.tableView animated:YES];
    hud.labelText = text;
        
    [[MintzatuAPIClient sharedClient] postPath:@"answer-request" parameters:params success:^(AFHTTPRequestOperation *operation, id responseObject) {
        [hud hide:YES];
        [[NSNotificationCenter defaultCenter] postNotificationName:FriendRequestNotification object:nil];
        [_controller deleteRequestWithCell:self];
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        [hud hide:YES];
    }];
}

- (void)setRequestFriend:(FriendRequest *)requestFriend
{
    _requestFriend = requestFriend;
    FriendContainerView *containerView = (FriendContainerView* )self.containerView;
    containerView.friendName = requestFriend.who;
}

@end
