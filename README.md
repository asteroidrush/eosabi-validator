# eosabi-validator

Web based validator for ABI files of [EOSIO](https://eos.io/) contracts.

More info about ABI content at developer portal:
[Understanding ABI Files](https://developers.eos.io/eosio-home/docs/the-abi#section-implicit-structs).

Made with [Groovy](http://groovy-lang.org/) and [Vaadin](https://vaadin.com/).

### Features
1. JSON validation: validator parses content with [jackson](https://github.com/FasterXML/jackson), if fails it will inform about error and set cursor at error location.
2. Basic validation of ABI structure, checks the availability of ABI fields.
3. Shows info about presented actions, structs, tables and types.
4. Perfom basic checks that all tables and actions have corresponding structs and all used types definied in types section.

### WIP
Validator may accept broken abi or find errors in correct file, please,
create an [ISSUE](https://github.com/codename-art/eosabi-validator/issues/new) with examples.
