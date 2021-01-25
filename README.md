
<img width="1054" alt="Screen Shot 2021-01-16 at 10 31 13 PM" src="https://user-images.githubusercontent.com/40813295/104831049-b1dff200-584a-11eb-97d8-ceefe2e88d9a.png">

<b></b>
<b></b>
<p align="center"> 
ChowNow is a full-stack Java application, built on the Spring Boot framework. This web app seeks to create a community of users who can easily create, share, find and save recipes based on the ingredients in their virtual pantries.
</p>

## Table of Contents
1. [Demo](https://github.com/chow-now/capstone#demo)
2. [Technologies Applied](https://github.com/chow-now/capstone#technologies)
3. [Setup Instruction](https://github.com/chow-now/capstone#setup-instruction)
4. [Contribution](https://github.com/chow-now/capstone#contribution)
### Demo
[ChowNow.xyz](https://www.chownow.xyz)

![chownow-demo](https://user-images.githubusercontent.com/40813295/105200128-1e861580-5b05-11eb-9dda-1f61da491904.gif)

                         
### Technologies
- Oauth2 [link](https://oauth.net/2/)
- AWS S3 [link](https://aws.amazon.com/s3/)
- Spoonacular API [link](https://spoonacular.com/food-api)
- Bootstrap [link](https://getbootstrap.com/)
- jQuery [link](https://jquery.com/)
- Spring Boot 
- Hibernate ORM + JPA
- Thymeleaf
- MySQL


### Setup Instruction

1. Clone this repo locally.
1. Create an `application.properties` file with valid credentials for a MySQL connection
1. Make sure to start a MySQL server.
1. This project requires an AWS S3 Bucket and access keys
1. This project requires Spoon API access key
1. This project requires Google Oauth 2 setup and client keys
1. Add any API keys needed to test the full functionality. Use the `application.properties.example` as a template.
1. Seed file will run on initial startup to populate data in the database.

### Contribution
**This list is in alphabetical order**

<details>
  <summary>Michael Klanica <a href="https://github.com/michaelklanica" target="_blank">GitHub</a></summary>

  1. Implementation of dynamic form using Thymeleaf.
  2. Integration of AWS S3 for storing images. 
  3. Styling form using CSS and Bootstrap.
  4. Accessing database info using Ajax requests.
</details>

<details>
  <summary>Sahara Tijol  <a href="https://github.com/saharatijol" target="_blank">GitHub</a></summary>
  
  1. Integration of Spoonacular API through AJAX requests and hidden forms  
  2. Implement fetching data from Spoonacular API from front-end then transfer that data to our back-end.
  3. Implemented search functionality using a single search input form that retrieves results from 2 sources.
  4. Styled recipes/index view using CSS, JQuery and Bootstrap 
  5. Remote server and database setup/management and deployment.
</details>

<details>
  <summary>Robert De laRosa <a href="https://github.com/rdelarosa3" target="_blank">GitHub</a></summary>

  1. Implementation of Spring Security 
  2. Integration of AWS S3 for remote file storage
  3. Implementation and setup for Oauth2 with Google 
  4. UX/UI design using JS, CSS, JQuery, and Bootstrap libraries
  5. Creation and admin dashboard with DataTables plugin
</details>
 
![CHOW 360](https://user-images.githubusercontent.com/40813295/105704661-c1a7a800-5ed4-11eb-90c6-51ff68b6606e.png)
