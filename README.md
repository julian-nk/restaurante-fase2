# restaurante-fase2
Projeto da fase 2 do Tech Challenge 

## Diagrama Entidade-Relacionamento

```mermaid
erDiagram
    ADDRESS {
        bigint id PK
        varchar street
        varchar number
        varchar complement "opcional"
        varchar neighborhood
        varchar city
        char state "UF, 2 letras"
        varchar zip_code "CEP, 8 dígitos"
    }

    PERSON {
        bigint id PK
        varchar name
        varchar email UK "único, case-insensitive"
        char cpf UK "11 dígitos"
        bigint address_id FK "opcional"
    }

    USER_TYPE {
        bigint id PK
        varchar name UK "único, case-insensitive"
        varchar code UK
    }

    APP_USER {
        bigint id PK
        bigint person_id FK, UK
        varchar login UK "único, case-insensitive"
        varchar password_hash
    }

    APP_USER_TYPE {
        bigint app_user_id PK, FK
        bigint user_type_id PK, FK
    }

    CUSTOMER {
        bigint person_id PK, FK
        timestamptz created_on
    }

    RESTAURANT_OWNER {
        bigint person_id PK, FK
        timestamptz created_on
        varchar business_email
    }

    RESTAURANT {
        bigint id PK
        varchar name
        char cnpj UK "14 dígitos"
        varchar cuisine_type
        bigint owner_id FK
        bigint address_id FK, UK
    }

    OPENING_HOURS {
        bigint id PK
        bigint restaurant_id FK
        smallint day_of_week "1 = segunda ... 7 = domingo"
        time opening_time
        time closing_time
    }

    MENU_ITEM {
        bigint id PK
        bigint restaurant_id FK
        varchar name
        text description
        numeric price "10,2 e maior ou igual a 0"
        boolean dine_in_only "padrão false"
        varchar photo_url "caminho da foto"
    }

    ADDRESS          |o--o{ PERSON           : "endereço de"
    PERSON           ||--o| APP_USER         : "possui acesso"
    APP_USER         ||--o{ APP_USER_TYPE    : "tem"
    USER_TYPE        ||--o{ APP_USER_TYPE    : "atribuído a"
    PERSON           ||--o| CUSTOMER         : "pode ser"
    PERSON           ||--o| RESTAURANT_OWNER : "pode ser"
    RESTAURANT_OWNER ||--o{ RESTAURANT       : "é dono de"
    ADDRESS          ||--o| RESTAURANT       : "localiza"
    RESTAURANT       ||--o{ OPENING_HOURS    : "funciona em"
    RESTAURANT       ||--o{ MENU_ITEM        : "oferece"
```