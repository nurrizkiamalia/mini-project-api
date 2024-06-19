# Mini Project API Endpoints
Database: https://github.com/tizetasmnd/miniproject-database

## Feature 1: Event Discovery, Transaction, and Reviews

### 1. Event Discovery and Event Details

#### (a) GET /events

- **Description**: Retrieve a list of events.
- **Query Parameters**: `category`, `location`, `page`, `limit`, `search`
- **Response**: List of events with pagination and filtering.

    **Success Response:**
    ```json
    {
        "events": [
        {
            "id": 1,
            "name": "Music Concert",
            "description": "An amazing music concert.",
            "price": 500000,
            "date": "2024-07-01",
            "time": "19:00:00",
            "location": "Jakarta",
            "available_seats": 150,
            "event_type": "Paid",
            "category": "Music",
            "event_picture": "http://example.com/image.jpg"
        }
        ],
        "pagination": {
        "page": 1,
        "limit": 10,
        "totalPages": 5
        }
    }
    ```
    **Failed Response:**
    ``` json
    {
        "error": "Invalid query parameters"
    }
    ```

#### (b) GET /events/{event_id}

- **Description**: Retrieve details of a specific event.
- **Path Parameters:**: `event_id`
- **Response**: Event details including name, description, price, date, time, location, available seats, ticket types, and picture.

    **Success Response:**
    ```json
        {
            "id": 1,
            "name": "Music Concert",
            "description": "An amazing music concert.",
            "price": 500000,
            "date": "2024-07-01",
            "time": "19:00:00",
            "location": "Jakarta",
            "available_seats": 150,
            "event_type": "Paid",
            "category": "Music",
            "event_picture": "http://example.com/image.jpg"
        }
    ```

    **Failed Response:**
    ``` json
    {
        "error": "Event not found"
    }
    ```

#### (c) POST /tickets

- **Description**: Purchase tickets for an event.
- **Request Body:**:
    ```json
    {
        "event_id": 1,
        "user_id": 2,
        "ticket_price": 500000,
        "discount_applied": 50000,
        "final_price": 450000
    }
    ```
- **Response**: Ticket purchase confirmation.

    **Success Response:**
    ```json
        {
            "ticket_id": 1,
            "event_id": 1,
            "user_id": 2,
            "ticket_price": 500000,
            "discount_applied": 50000,
            "final_price": 450000,
            "purchase_date": "2024-06-19T14:48:00Z"
        }
    ```

    **Failed Response:**
    ``` json
    {
        "error": "Insufficient available seats"
    }
    ```

---

### 2. Event Transaction and Promotion

#### (a) POST /events

- **Description**: Create a new event.
- **Request Body:**:
    ```json
    {
        "organizer_id": 2,
        "name": "Music Concert",
        "description": "An amazing music concert.",
        "price": 500000,
        "date": "2024-07-01",
        "time": "19:00:00",
        "location": "Jakarta",
        "available_seats": 150,
        "event_type": "Paid",
        "category": "Music",
        "event_picture": "http://example.com/image.jpg"
    }
    ```
- **Response**: Created event details.

    **Success Response:**
    ```json
    {
        "id": 1,
        "organizer_id": 2,
        "name": "Music Concert",
        "description": "An amazing music concert.",
        "price": 500000,
        "date": "2024-07-01",
        "time": "19:00:00",
        "location": "Jakarta",
        "available_seats": 150,
        "event_type": "Paid",
        "category": "Music",
        "event_picture": "http://example.com/image.jpg",
        "created_at": "2024-06-19T14:48:00Z"
    }
    ```
    **Failed Response:**
    ```json
    {
        "error": "Invalid input data"
    }
    ```

#### (b) POST /promotions

- **Description**: Create a promotion with discount vouchers.
- **Request Body:**:
    ```json
    {
        "user_id": 2,
        "discount_percentage": 10.00,
        "expiry_date": "2024-12-31"
    }
    ```
- **Response**: Promotion creation confirmation.

    **Success Response:**
    ```json
    {
        "promotion_id": 1,
        "user_id": 2,
        "discount_percentage": 10.00,
        "expiry_date": "2024-12-31",
        "created_at": "2024-06-19T14:48:00Z"
    }
    ```
    **Failed Response:**
    ```json
    {
        "error": "Invalid input data"
    }
    ```
---

### 3. Event Reviews and Ratings

#### (a) POST /reviews

- **Description**: Leave a review and rating for an event.
- **Request Body:**:
    ```json
    {
        "event_id": 1,
        "user_id": 2,
        "rating": 4,
        "review_text": "Great event!"
    }
    ```
- **Response**: Review submission confirmation.

    **Success Response:**
    ```json
    {
        "review_id": 1,
        "event_id": 1,
        "user_id": 2,
        "rating": 4,
        "review_text": "Great event!",
        "created_at": "2024-06-19T14:48:00Z"
    }
    ```
    **Failed Response:**
    ```json
    {
        "error": "Invalid input data"
    }
    ```
---
## Feature 2: User Authentication, Referral System, and Management Dashboard

### 1. User Authentication and Authorization

#### (a) POST /register

- **Description**: Register a new user.
- **Request Body:**:
    ```json
    {
        "first_name": "John",
        "last_name": "Doe",
        "email": "john.doe@example.com",
        "password": "securepassword",
        "referral_code": "REF12345"
    }
    ```
- **Response**: Registration confirmation and generated referral code.

    **Success Response:**
    ```json
    {
        "user_id": 1,
        "referral_code": "REF67890",
        "created_at": "2024-06-19T14:48:00Z"
    }
    ```
    **Failed Response:**
    ```json
    {
        "error": "Email already exists"
    }
    ```

#### (b) POST /login

- **Description**: Authenticate a user.
- **Request Body:**:
    ```json
    {
        "email": "john.doe@example.com",
        "password": "securepassword"
    }
    ```
- **Response**: Registration confirmation and generated referral code.

    **Success Response:**
    ```json
    {
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    }
    ```
    **Failed Response:**
    ```json
    {
        "error": "Invalid email or password"
    }
    ```

#### (c) GET /users/{user_id}

- **Description**: Retrieve user profile details.
- **Path Parameters:**: `user_id`
- **Response**: User profile information.
  
    **Success Response:**
    ```json
    {
        "id": 1,
        "first_name": "John",
        "last_name": "Doe",
        "email": "john.doe@example.com",
        "points": 20000,
        "profile_picture": "http://example.com/profile.jpg",
        "created_at": "2024-06-19T14:48:00Z"
    }
    ```
    **Failed Response:**
    ```json
    {
        "error": "User not found"
    }
    ```
---
### 2. Referral Number, Points, and Prizes

#### (a) POST /referrals

- **Description**: Use a referral code for registration.
- **Request Body:**:
    ```json
    {
        "referrer_id": 1,
        "referee_id": 2
    }
    ```
- **Response**: Referral usage confirmation and points awarded.

    **Success Response:**
    ```json
    {
        "referral_id": 1,
        "referrer_id": 1,
        "referee_id": 2,
        "points_earned": 10000,
        "points_expiry_date": "2024-09-19",
        "created_at": "2024-06-19T14:48:00Z"
    }
    ```
    **Failed Response:**
    ```json
    {
        "error": "Referral code invalid or already used"
    }
    ```

#### (b) GET /points/{user_id}

- **Description**: Retrieve user's points balance.
- **Path Parameters**: `user_id`
- **Response**: User points balance and expiry details.

    **Success Response:**
    ```json
    {
        "user_id": 1,
        "points_balance": 20000,
        "points": [
            {
                "points_earned": 10000,
                "expiry_date": "2024-09-19"
            },
            {
                "points_earned": 10000,
                "expiry_date": "2024-12-19"
            }
        ]
    }
    ```
    **Failed Response:**
    ```json
    {
        "error": "User not found"
    }
    ```

#### (c) POST /points/redemption

- **Description**: Redeem points for ticket discounts.
- **Request Body:**:
    ```json
    {
        "user_id": 1,
        "ticket_id": 2,
        "points_redeemed": 5000
    }
    ```
- **Response**: Points redemption confirmation.

    **Success Response:**
    ```json
    {
        "redemption_id": 1,
        "user_id": 1,
        "ticket_id": 2,
        "points_redeemed": 5000,
        "created_at": "2024-06-19T14:48:00Z"
    }
    ```
    **Failed Response:**
    ```json
    {
        "error": "Insufficient points balance"
    }
    ```
---

### 3. Event Management Dashboard

#### (a) GET /dashboard/events

- **Description**: Retrieve events managed by an organizer.
- **Query Parameters**: `organizer_id`, `year`, `month`, `day`
- **Response**:  List of events with statistics.

    **Success Response:**
    ```json
    {
        "events": [
            {
            "id": 1,
            "name": "Music Concert",
            "date": "2024-07-01",
            "total_tickets_sold": 50,
            "total_revenue": 25000000
            }
        ]
    }
    ```
    **Failed Response:**
    ```json
    {
        "error": "Invalid query parameters"
    }
    ```

#### (b) GET /dashboard/statistics

- **Description**: Retrieve statistical data for an organizer's events.
- **Query Parameters**: `organizer_id`
- **Response**: Event statistics in graphical format.

    **Success Response:**
    ```json
    {
        "organizer_id": 1,
        "statistics": {
            "total_events": 10,
            "total_tickets_sold": 500,
            "total_revenue": 125000000
        }
    }
    ```
    **Failed Response:**
    ```json
    {
        "error": "Organizer not found"
    }
    ```

#### (c) GET /dashboard/reports

- **Description**: Retrieve reports for an organizer's events.
- **Query Parameters**: `organizer_id`, `year`, `month`, `day`
- **Response**: Detailed reports of events.

    **Success Response:**
    ```json
    {
        "reports": [
            {
            "event_id": 1,
            "name": "Music Concert",
            "date": "2024-07-01",
            "tickets_sold": 50,
            "revenue": 25000000
            }
        ]
    }
    ```
    **Failed Response:**
    ```json
    {
        "error": "Invalid query parameters"
    }
    ```
---

## Additional Endpoints

#### (a) GET /categories

- **Description**: Retrieve a list of event categories.
- **Response**: List of categories.

    **Success Response:**
    ```json
    {
        "categories": ["Music", "Sports", "Conference", "Workshop", "Theatre", "Exhibition", "Festival", "Others"]
    }
    ```
    **Failed Response:**
    ```json
    {
        "error": "Failed to retrieve categories"
    }
    ```

#### (b) GET /locations

- **Description**: Retrieve a list of event locations.
- **Response**: List of locations.

    **Success Response:**
    ```json
    {
        "locations": ["Jakarta", "Bandung", "Surabaya", "Yogyakarta", "Bali"]
    }
    ```
    **Failed Response:**
    ```json
    {
        "error": "Failed to retrieve locations"
    }
    ```




