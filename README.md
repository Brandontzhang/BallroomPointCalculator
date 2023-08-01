# BallroomPointCalculator

## Project Description
### Problem:
In order to determine what level they are at, collegiate ballroom dancers use their placements at competitions to determine whether or not they should advance to the next level. 
The points are determined as follows:
1st : 3 points
2nd : 2 Points
3rd : 1 Point
4-8th : 1 Point (If there was a quarter final)
Additionally, points are only considered when there is a semi-final or more at the competition. 

The placements are tracked on the website results.o2cm.com, but are not formatted in a way that lets competitors easily count their scores.

This tool parses through HTML by directly querying results.o2cm.com with the competitors name, and displays the points they have gained in a table.

## Tech stack
### HTML Parser
- Built with Java 
- Uses JSoup to parse HTML files

### Frontend Display
- Built using React
- Using Ant Design components
