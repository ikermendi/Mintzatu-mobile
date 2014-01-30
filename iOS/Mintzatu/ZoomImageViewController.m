//
//  ZoomImageViewcontrollerViewController.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 10/09/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "ZoomImageViewController.h"

#import "SKBounceAnimation.h"

@implementation UIImageView (Expand)

- (void)setup
{
    UITapGestureRecognizer *tapRecognizer = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapped)];
    [self addGestureRecognizer:tapRecognizer];
}

- (void)tapped
{
    ZoomImageViewController * imageBrowser = [[ZoomImageViewController alloc] initWithNibName:@"ZoomImageView" bundle:nil];
    imageBrowser.senderView = self;
    if(self.image)
        [imageBrowser presentFromRootViewController];
}




@end

@interface ZoomImageViewController () <UIScrollViewDelegate>
{
    UIImageView *_photoImageView;
    __weak IBOutlet UIScrollView *_scrollView;
}
@end

@implementation ZoomImageViewController

- (void)dealloc
{
    _photoImageView = nil;
    _rootViewController = nil;
    _senderView = nil;
    _scrollView = nil;
}

/*
 * Con este codigo harto conseguiamos una animacion guapa para mostrar la foto. Está sin acabar.
 */

/*- (void)loadView
{
    [super loadView];
    
    CGRect windowBounds = [[UIScreen mainScreen] bounds];
    CGRect windowFrame = CGRectMake(windowBounds.origin.x, windowBounds.origin.y, windowBounds.size.width, windowBounds.size.height - 20);
    
    // Compute Original Frame Relative To Screen
    CGRect newFrame = [_senderView convertRect:CGRectMake(windowBounds.origin.x, windowBounds.origin.y - 20, windowBounds.size.width, windowBounds.size.height - 20) toView:nil];
    newFrame.origin = CGPointMake(newFrame.origin.x, newFrame.origin.y);
    newFrame.size = _senderView.frame.size;
    _originalFrameRelativeToScreen = newFrame;
    
    self.view = [[UIView alloc] initWithFrame:windowFrame];
    self.view.backgroundColor = [UIColor grayColor];
    
    _blackMask = [[UIView alloc] initWithFrame:windowFrame];
    _blackMask.backgroundColor = [UIColor blackColor];
    _blackMask.alpha = 0.0f;
    _blackMask.autoresizingMask = UIViewAutoresizingFlexibleHeight | UIViewAutoresizingFlexibleWidth;
    //[self.view insertSubview:_blackMask atIndex:0];
    
    _photoImageView = [[UIImageView alloc] initWithFrame:_originalFrameRelativeToScreen];
    _photoImageView.image = _senderView.image;
    
    [self.view insertSubview:_photoImageView atIndex:1];
    
    [UIView animateWithDuration:1.0f animations:^{
        _blackMask.alpha = 1.0f;
        _senderView.hidden = YES;
        _blackMask.alpha = 1.0f;
        CGRect frame = _photoImageView.frame;
        frame.origin = CGPointMake(_senderView.frame.origin.x, windowFrame.size.height/2 - frame.size.height/2);
        _photoImageView.frame = frame;
    } completion:^(BOOL finished) {
        
            _doneButton = [UIButton buttonWithType:UIButtonTypeSystem];
            [_doneButton setTitle:@"Cerrar" forState:UIControlStateNormal];
            _doneButton.frame = CGRectMake(windowBounds.size.width - (51.0f + 9.0f),15.0f, 51.0f, 26.0f);
            
            [_doneButton addTarget:self
                            action:@selector(close:)
                  forControlEvents:UIControlEventTouchUpInside];
            
         [self.view addSubview:_doneButton];
        //[self.view addSubview:self.scrollView];
    }];
    
    [_photoImageView removeFromSuperview];
}*/

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.screenName = @"ZoomImageViewController";
    self.wantsFullScreenLayout = NO;
    
    if (floor(NSFoundationVersionNumber) > NSFoundationVersionNumber_iOS_6_1) {
        self.edgesForExtendedLayout = YES;
    }
    
    _photoImageView = [[UIImageView alloc] initWithImage:_senderView.image];
    
    _scrollView.delegate = self;
    _scrollView.backgroundColor = [UIColor clearColor];
    
    [_scrollView addSubview:_photoImageView];
    
    // Tell the scroll view the size of the contents
    _scrollView.contentSize = _photoImageView.image.size;
    
    UITapGestureRecognizer *doubleTapRecognizer = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(scrollViewDoubleTapped:)];
    doubleTapRecognizer.numberOfTapsRequired = 2;
    doubleTapRecognizer.numberOfTouchesRequired = 1;
    [_scrollView addGestureRecognizer:doubleTapRecognizer];
    
    UITapGestureRecognizer *twoFingerTapRecognizer = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(scrollViewTwoFingerTapped:)];
    twoFingerTapRecognizer.numberOfTapsRequired = 1;
    twoFingerTapRecognizer.numberOfTouchesRequired = 2;
    [_scrollView addGestureRecognizer:twoFingerTapRecognizer];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    // Set up the minimum & maximum zoom scales
    CGRect scrollViewFrame = _scrollView.frame;
    CGFloat scaleWidth = scrollViewFrame.size.width / _scrollView.contentSize.width;
    CGFloat scaleHeight = scrollViewFrame.size.height / _scrollView.contentSize.height;
    CGFloat minScale = MIN(scaleWidth, scaleHeight);
    
    _scrollView.minimumZoomScale = minScale;
    _scrollView.maximumZoomScale = 2.0f;
    _scrollView.zoomScale = minScale;
    
    [self centerScrollViewContents];
}


#pragma mark Show
- (void)presentFromRootViewController
{
    UIViewController *rootViewController = [UIApplication sharedApplication].keyWindow.rootViewController;
    [self presentFromViewController:rootViewController];
}

- (void)presentFromViewController:(UIViewController *)controller
{
    _rootViewController = controller;
    [_rootViewController presentViewController:self animated:YES completion:nil];
}


#pragma mark Close

- (IBAction)close:(id)sender
{
    [self dismissViewControllerAnimated:YES completion:nil];
}


- (CGRect)centerFrameFromImage:(UIImage*) image {
    if(!image) return CGRectZero;
    
    CGRect windowBounds = _rootViewController.view.bounds;
    CGSize newImageSize = [self imageResizeBaseOnWidth:windowBounds
                           .size.width oldWidth:image
                           .size.width oldHeight:image.size.height];
    // Just fit it on the size of the screen
    newImageSize.height = MIN(windowBounds.size.height,newImageSize.height);
    return CGRectMake(0.0f, windowBounds.size.height/2 - newImageSize.height/2, newImageSize.width, newImageSize.height);
}

- (CGSize)imageResizeBaseOnWidth:(CGFloat) newWidth oldWidth:(CGFloat) oldWidth oldHeight:(CGFloat)oldHeight {
    CGFloat scaleFactor = newWidth / oldWidth;
    CGFloat newHeight = oldHeight * scaleFactor;
    return CGSizeMake(newWidth, newHeight);
}


#pragma mark ScrollView

- (void)centerScrollViewContents {
    CGSize boundsSize = _scrollView.bounds.size;
    CGRect contentsFrame = _photoImageView.frame;
    
    if (contentsFrame.size.width < boundsSize.width) {
        contentsFrame.origin.x = (boundsSize.width - contentsFrame.size.width) / 2.0f;
    } else {
        contentsFrame.origin.x = 0.0f;
    }
    
    if (contentsFrame.size.height < boundsSize.height) {
        contentsFrame.origin.y = (boundsSize.height - contentsFrame.size.height) / 2.0f;
    } else {
        contentsFrame.origin.y = 0.0f;
    }
    _photoImageView.frame = contentsFrame;
}

- (void)scrollViewDoubleTapped:(UITapGestureRecognizer*)recognizer {
    // Get the location within the image view where we tapped
    CGPoint pointInView = [recognizer locationInView:_photoImageView];
    
    // Get a zoom scale that's zoomed in slightly, capped at the maximum zoom scale specified by the scroll view
    CGFloat newZoomScale = _scrollView.zoomScale * 1.5f;
    newZoomScale = MIN(newZoomScale, _scrollView.maximumZoomScale);
    
    // Figure out the rect we want to zoom to, then zoom to it
    CGSize scrollViewSize = _scrollView.bounds.size;
    
    CGFloat w = scrollViewSize.width / newZoomScale;
    CGFloat h = scrollViewSize.height / newZoomScale;
    CGFloat x = pointInView.x - (w / 2.0f);
    CGFloat y = pointInView.y - (h / 2.0f);
    
    CGRect rectToZoomTo = CGRectMake(x, y, w, h);
    
    [_scrollView zoomToRect:rectToZoomTo animated:YES];
}

- (void)scrollViewTwoFingerTapped:(UITapGestureRecognizer*)recognizer {
    // Zoom out slightly, capping at the minimum zoom scale specified by the scroll view
    CGFloat newZoomScale = _scrollView.zoomScale / 1.5f;
    newZoomScale = MAX(newZoomScale, _scrollView.minimumZoomScale);
    [_scrollView setZoomScale:newZoomScale animated:YES];
}

- (UIView*)viewForZoomingInScrollView:(UIScrollView *)scrollView {
    // Return the view that we want to zoom
    return _photoImageView;
}

- (void)scrollViewDidZoom:(UIScrollView *)scrollView {
    // The scroll view has zoomed, so we need to re-center the contents
    [self centerScrollViewContents];
}


@end
