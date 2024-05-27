### Hexlet tests and linter status, maintainability and test coverage:
[![Actions Status](https://github.com/BOMBYASCHER/java-project-99/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/BOMBYASCHER/java-project-99/actions)
[![Maintainability](https://api.codeclimate.com/v1/badges/e695129a02d515730a5c/maintainability)](https://codeclimate.com/github/BOMBYASCHER/java-project-99/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/e695129a02d515730a5c/test_coverage)](https://codeclimate.com/github/BOMBYASCHER/java-project-99/test_coverage)

# Task Manager
_**Task Manager is the final training project that summarizes the entire training and covers all key aspects of website development, including build (gradle) and deploy.**_

## Description
Task Manager is a task management system similar to http://www.redmine.org/. It allows you to set tasks, assign executors and change their statuses. Registration and authentication are required to work with the system.

## Objective
To be able to build complete websites from scratch using modern technology - something that people come to Hexlet for. This project summarizes the entire learning experience and covers all key aspects of website development, including build (gradle) and deploy.

A big focus of this project is on creating entities using ORM and describing the relationships between them (o2m, m2m). Students will have to design models and their mapping to a database. This makes it possible to increase the level of abstraction and to operate not with raw data, but with linked sets of entities with convenient (semantic) access to dependent entities.

For a higher level of automation, the project uses resource routing, which allows to unify and simplify the work with typical CRUD-operations. In this way, the correct view of URL formation and their relationship to each other is developed.

As soon as users appear on the site with the ability to create something, authorization is required. Authorization is the process of granting rights to actions on resources and controlling their execution. It is often invoked when trying to change forbidden things, such as the settings of someone else's user. Authorization mechanism is an important part of a web application that is given considerable attention in a project.

One of the important and typical tasks in API design is data filtering. If this task is approached incorrectly, it turns into a big lump of confusing code. The project allows you to work out this point using the right way to solve this task.

Exploitation of the project is as important as development. A developer must be sure that his code works correctly, and for this purpose he writes tests. But tests cannot guarantee 100% performance, that's why you need a mechanism that traces errors occurring in production and notifies you about them. This task is solved by error collectors, for example, Rollbar. Such a service collects errors in real time and sends information about them to any possible channels, such as slack or mail. The project is a good way to practice integrating such a service.