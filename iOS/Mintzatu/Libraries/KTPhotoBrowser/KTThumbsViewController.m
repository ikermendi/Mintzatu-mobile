//
//  KTThumbsViewController.m
//  KTPhotoBrowser
//
//  Created by Kirby Turner on 2/3/10.
//  Copyright 2010 White Peak Software Inc. All rights reserved.
//

#import "KTThumbsViewController.h"
#import "KTThumbsView.h"
#import "KTThumbView.h"
#import "KTPhotoScrollViewController.h"


@interface KTThumbsViewController (Private) <UIScrollViewDelegate>
@end


@implementation KTThumbsViewController

@synthesize dataSource = dataSource_;

- (void)dealloc {
   [scrollView_ release], scrollView_ = nil;
   
   [super dealloc];
}

/*- (void)loadView
{
    [super loadView];
    if (floor(NSFoundationVersionNumber) > NSFoundationVersionNumber_iOS_6_1) {
        //Odio iOS7
        self.edgesForExtendedLayout = UIRectEdgeNone;
    } else {
        [self setWantsFullScreenLayout:YES];
    }
}*/

- (void)loadView {
    // Make sure to set wantsFullScreenLayout or the photo
    // will not display behind the status bar.
    [self setWantsFullScreenLayout:YES];
    
    KTThumbsView *scrollView = [[KTThumbsView alloc] initWithFrame:CGRectZero];
    [scrollView setDataSource:self];
    [scrollView setController:self];
    [scrollView setScrollsToTop:YES];
    [scrollView setScrollEnabled:YES];
    [scrollView setAlwaysBounceVertical:YES];
    [scrollView setBackgroundColor:[UIColor whiteColor]];
    
    if ([dataSource_ respondsToSelector:@selector(thumbsHaveBorder)]) {
        [scrollView setThumbsHaveBorder:[dataSource_ thumbsHaveBorder]];
    }
    
    if ([dataSource_ respondsToSelector:@selector(thumbSize)]) {
        [scrollView setThumbSize:[dataSource_ thumbSize]];
    }
    
    if ([dataSource_ respondsToSelector:@selector(thumbsPerRow)]) {
        [scrollView setThumbsPerRow:[dataSource_ thumbsPerRow]];
    }
    
    
    // Set main view to the scroll view.
    [self setView:scrollView];
    
    // Retain a reference to the scroll view.
    scrollView_ = scrollView;
    [scrollView_ retain];
    
    // Release the local scroll view reference.
    [scrollView release];
}


- (void)viewDidLoad
{
    [super viewDidLoad];
    
    /*self.view.backgroundColor = [UIColor backgroundBeigeLight];
    
    UIView *v = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, 4)];
    v.backgroundColor = [UIColor mintzatuOrange];
    [self.view addSubview:v];
    [v release];
    
    KTThumbsView *scrollView = [[KTThumbsView alloc] initWithFrame:CGRectMake(0, 4, self.view.frame.size.width, self.view.frame.size.height - 4 - 49 - 44 - 20)];
    [scrollView setDataSource:self];
    [scrollView setController:self];
    [scrollView setScrollsToTop:YES];
    [scrollView setScrollEnabled:YES];
    [scrollView setAlwaysBounceVertical:YES];
    [scrollView setBackgroundColor:[UIColor whiteColor]];
    
    scrollView.delegate = self;
    
    if ([dataSource_ respondsToSelector:@selector(thumbsHaveBorder)]) {
        [scrollView setThumbsHaveBorder:[dataSource_ thumbsHaveBorder]];
    }
    
    if ([dataSource_ respondsToSelector:@selector(thumbSize)]) {
        [scrollView setThumbSize:[dataSource_ thumbSize]];
    }
    
    if ([dataSource_ respondsToSelector:@selector(thumbsPerRow)]) {
        [scrollView setThumbsPerRow:[dataSource_ thumbsPerRow]];
    }
    
    
    // Set main view to the scroll view.
    [self.view addSubview:scrollView];
    
    // Retain a reference to the scroll view.
    scrollView_ = scrollView;
    [scrollView_ retain];
    
    // Release the local scroll view reference.
    [scrollView release];*/
}

- (void)didReceiveMemoryWarning {
	// Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
	
	// Release any cached data, images, etc that aren't in use.
}

- (void)viewWillAppear:(BOOL)animated {
  // The first time the view appears, store away the current translucency so we can reset on pop.
  UINavigationBar *navbar = [[self navigationController] navigationBar];
  if (!viewDidAppearOnce_) {
    viewDidAppearOnce_ = YES;
    navbarWasTranslucent_ = [navbar isTranslucent];
  }
  // Then ensure translucency to match the look of Apple's Photos app.
  [navbar setTranslucent:YES];
  [super viewWillAppear:animated];
}

- (void)viewWillDisappear:(BOOL)animated {
  // Restore old translucency when we pop this controller.
  UINavigationBar *navbar = [[self navigationController] navigationBar];
  [navbar setTranslucent:navbarWasTranslucent_];
  [super viewWillDisappear:animated];
}

- (void)viewDidUnload {
	// Release any retained subviews of the main view.
	// e.g. self.myOutlet = nil;
}

- (void)willLoadThumbs {
   // Do nothing by default.
}

- (void)didLoadThumbs {
   // Do nothing by default.
}

- (void)reloadThumbs {
   [self willLoadThumbs];
   [scrollView_ reloadData];
   [self didLoadThumbs];
}

- (void)setDataSource:(id <KTPhotoBrowserDataSource>)newDataSource {
   dataSource_ = newDataSource;
   [self reloadThumbs];
}

- (void)didSelectThumbAtIndex:(NSUInteger)index {
   KTPhotoScrollViewController *newController = [[KTPhotoScrollViewController alloc] 
                                                        initWithDataSource:dataSource_ 
                                                  andStartWithPhotoAtIndex:index];
    newController.showDetailView = _showDetailView;
   [[self navigationController] pushViewController:newController animated:YES];
   [newController release];
}

#pragma mark -
#pragma mark UIScrollViewdelegate

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView {
    float bottomEdge = scrollView.contentOffset.y + scrollView.frame.size.height;
    if (bottomEdge >= scrollView.contentSize.height) {
        [dataSource_ loadMoreElements:^(BOOL success) {
            [self reloadThumbs];
        }];
    }
}


#pragma mark -
#pragma mark KTThumbsViewDataSource

- (NSInteger)thumbsViewNumberOfThumbs:(KTThumbsView *)thumbsView
{
   NSInteger count = [dataSource_ numberOfPhotos];
   return count;
}

- (KTThumbView *)thumbsView:(KTThumbsView *)thumbsView thumbForIndex:(NSInteger)index
{
   KTThumbView *thumbView = [thumbsView dequeueReusableThumbView];
   if (!thumbView) {
      //thumbView = [[[KTThumbView alloc] initWithFrame:CGRectZero] autorelease];
      thumbView = [KTThumbView buttonWithType:UIButtonTypeCustom];
      [thumbView setController:self];
   }

   // Set thumbnail image.
   if ([dataSource_ respondsToSelector:@selector(thumbImageAtIndex:thumbView:)] == NO) {
      // Set thumbnail image synchronously.
      UIImage *thumbImage = [dataSource_ thumbImageAtIndex:index];
      [thumbView setThumbImage:thumbImage];
   } else {
      // Set thumbnail image asynchronously.
      [dataSource_ thumbImageAtIndex:index thumbView:thumbView];
   }
   
   return thumbView;
}


@end
