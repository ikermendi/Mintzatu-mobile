//
//  EzarpenakViewController.m
//  Mintzatu
//
//  Created by Iker Mendilibar Fernandez on 30/08/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "EzarpenakViewController.h"

#import "SimpleCell.h"
#import "MintzatuAPIClient.h"
#import "AppDelegate.h"

@interface EzarpenakViewController () <UIAlertViewDelegate>
@property (weak, nonatomic) IBOutlet UITableView *tableView;

@end

@implementation EzarpenakViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.screenName = @"EzarpenakViewController";
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    NSIndexPath *selection = [self.tableView indexPathForSelectedRow];
    if (selection) {
        [self.tableView deselectRowAtIndexPath:selection animated:YES];
    }
}

#pragma mark - TableView

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSUInteger row = indexPath.row;
    NSUInteger section = indexPath.section;
    
    if (section == 0) {
        if (row == 0) {
            [self performSegueWithIdentifier:@"social" sender:nil];
        } else {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Abisua" message:@"Saioa itxi nahi duzu?" delegate:self cancelButtonTitle:@"Ezeztatu" otherButtonTitles:@"Irten", nil];
            [alert show];
        }
    } else {
        if (row == 0) {
            [self performSegueWithIdentifier:@"license" sender:nil];
        } else {
            [self performSegueWithIdentifier:@"babesleak" sender:nil];
        }
        
    }
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 2;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if (section == 0)
        return 2;
    return 2;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    SimpleCell *cell = [tableView dequeueReusableCellWithIdentifier:@"SimpleCell"];
    if (cell == nil) {
        NSArray *nib = [[NSBundle mainBundle] loadNibNamed:@"SimpleCellView" owner:self options:nil];
        cell = (SimpleCell *)[nib objectAtIndex:0];
        cell.borderWidth = 1.0f;
        cell.borderColor = [UIColor backgroundBeige];
    }
    
    if (indexPath.section == 0) {
        if (indexPath.row == 0) {
            cell.type = SimpleCellTypeTop;
            cell.cellTextLabel.text = @"Sare sozialak";
        } else {
            cell.type = SimpleCellTypeBottom;
            cell.cellTextLabel.text = @"Saioa itxi";
        }
    } else {
        if (indexPath.row == 0) {
            cell.type = SimpleCellTypeSingle;
            cell.cellTextLabel.text = @"Lizentziak";
        }else{
            cell.type = SimpleCellTypeSingle;
            cell.cellTextLabel.text = @"Babesleak";
        }
        
    }
    

    return cell;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    UIView *v = [[UIView alloc] init];
    v.backgroundColor = [UIColor clearColor];
    return v;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 56.0f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    return 10.0f;
}

#pragma mark AlertView

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (buttonIndex != alertView.cancelButtonIndex) {
        [MintzatuAPIClient logout];
        AppDelegate *delegate = (AppDelegate*) [[UIApplication sharedApplication] delegate];
        [delegate loadLoginController];
    }
}


@end
