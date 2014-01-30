//
//  TwitterAcountsViewController.m
//  Mintzatu
//
//  Created by Sergio Garcia on 19/12/13.
//  Copyright (c) 2013 Irontec S.L. All rights reserved.
//

#import "TwitterAcountsViewController.h"
#import "AcountNameCell.h"
#import "SocialAccountWrapper.h"

#import <Accounts/Accounts.h>

@interface TwitterAcountsViewController ()
{
    NSMutableArray *names;
    bool userSelected;
}

@end

@implementation TwitterAcountsViewController

@synthesize twCell, twArrayAcounts, socialWrapper, topSeparator;


- (void)viewDidLoad
{
    [super viewDidLoad];
    
    
    names = [[NSMutableArray alloc]init];
    userSelected = NO;
    
    //Cargar todos los nombres de usuario
    ACAccount *t;
    for(int x=0;x<twArrayAcounts.count;x++){
        t = [twArrayAcounts objectAtIndex:x];
        [names addObject:[NSString stringWithFormat:@"%@",t.username]];
    }

}

- (void)viewDidAppear:(BOOL)animated
{
//    CGRect frame = topSeparator.frame;
//    frame.origin.y = 4;
//    [topSeparator setFrame:frame];
//    CGRect tableFrame = _tableView.frame;
//    tableFrame.origin.y = 4;
//    [_tableView setFrame:tableFrame];
//    NSLog(@"Origin top: %f", topSeparator.frame.origin.y);
//    NSLog(@"Origin table: %f", _tableView.frame.origin.y);
}
- (void)viewWillDisappear:(BOOL)animated
{
    //Si no ha seleccionado nada, anulamos la activacion en la celda de Twitter de parent
    //Desactivamos Twitter
    if(!userSelected){
        twCell.switcher.on = NO;
        [socialWrapper deactivateTwitter];
    }
}

#pragma mark - TableView

- (NSInteger)numberOfSections
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return names.count;
}

- (UIView *) tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    UIView *headerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, tableView.bounds.size.width, 30)];
    [headerView setBackgroundColor:[UIColor clearColor]];
    return headerView;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    SimpleCell *cell = [tableView dequeueReusableCellWithIdentifier:@"acountNameCell"];
    if (cell == nil) {
        NSArray *nib = [[NSBundle mainBundle] loadNibNamed:@"SimpleCellView" owner:self options:nil];
        cell = (SimpleCell *)[nib objectAtIndex:0];
        cell.borderWidth = 1.0f;
        cell.borderColor = [UIColor backgroundBeige];
    }
    
    cell.type = SimpleCellTypeSingle;
    cell.cellTextLabel.text = [@"@" stringByAppendingString:[names objectAtIndex:indexPath.row]];
    
    if (indexPath.row == 0){
        cell.type = SimpleCellTypeTop;
        cell.cellTextLabel.text = [@"@" stringByAppendingString:[names objectAtIndex:indexPath.row]];
    } else if (indexPath.row == names.count - 1){
        cell.type = SimpleCellTypeBottom;
        cell.cellTextLabel.text = [@"@" stringByAppendingString:[names objectAtIndex:indexPath.row]];
    } else {
        cell.type = SimpleCellTypeMiddle;
        cell.cellTextLabel.text = [@"@" stringByAppendingString:[names objectAtIndex:indexPath.row]];
    }

    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 56.0f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    return 10.0f;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    ACAccount *t;
    t = [twArrayAcounts objectAtIndex:indexPath.row];
    //Activamos twitter y guardamos la cuentaf
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setBool:YES forKey:@"TwitterLogged"];
    [defaults synchronize];
    [socialWrapper setSelectedTwitterAcount:t.identifier];
    userSelected = YES;
    twCell.switcher.on = YES;
    [self.navigationController popViewControllerAnimated:YES];
}

@end
