#Summit

Overview:
In it's current state, Summit can abstract away most of the server-side and custom code required to build a simple web-based reporting solution for an SQL query. 

It also provides some fairly basic client-side HTML, CSS and javascript code as an example for using the Summit reporting functionality.

Future Goal:
In the short term, I really need to document the structure of Summit better, including some diagrams and some examples of how it all fits together so others can dive in and use it with minimal effort.
From there, I want to work on some issues in the reporting side of Summit, plus some additional reporting features I would like to implement. I'd also like to start separating out all server side reporting functionality into it's own JAR(s) for easier inclusion into other projects. After that, I'd like to start building a front-end for adding templates, and building pages for reporting.

Long term I'd like to have a Free/Libre and Open Source Software alternative to Oracle's Application Express.

Reporting Example:
The following screenshots show case some of the current reporting functionality of Summit. This example is driven from the 'src/main/resources/test_dml.sql'.

After running test_dml.sql:

1) Hit application '1', page '2' (defined via this URL: http://localhost:8080/summit/run/1/2) and you should see this screen:
![Screenshot](https://raw.github.com/VenKamikaze/summit/master/doc/screenshots/summit-intro-1.png)

2a) Out of the box, Summit provides pagination capability as we can see if we change the 'rows' drop-down to '5' and hit the 'Go' button:
![Screenshot](https://raw.github.com/VenKamikaze/summit/master/doc/screenshots/summit-intro-2a.png)

2b) Page 2 when hitting the blue 'next' arrow
![Screenshot](https://raw.github.com/VenKamikaze/summit/master/doc/screenshots/summit-intro-2b.png)

3) Summit provides filtering capability across all columns in a report:
![Screenshot](https://raw.github.com/VenKamikaze/summit/master/doc/screenshots/summit-intro-3.png)

4) Plus a few basic filtering options (exact match, contains, starts with, ends with):
![Screenshot](https://raw.github.com/VenKamikaze/summit/master/doc/screenshots/summit-intro-4.png)

5) In it's current state, Summit can link off to other pages on selection/clicking of a row. This can be customised with some minimal changes to the example client side javascript code included in test_dml.sql.
