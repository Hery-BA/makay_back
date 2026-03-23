-- V1__init_schema.sql
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Admin users
CREATE TABLE admin_users (
                             id         UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                             email      VARCHAR(255) NOT NULL UNIQUE,
                             password   VARCHAR(255) NOT NULL,
                             full_name  VARCHAR(255) NOT NULL,
                             role       VARCHAR(50)  NOT NULL DEFAULT 'ADMIN',
                             created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                             updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Expeditions
CREATE TABLE expeditions (
                             id               UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                             slug             VARCHAR(255) NOT NULL UNIQUE,
                             title_fr         VARCHAR(255) NOT NULL,
                             title_en         VARCHAR(255),
                             description_fr   TEXT,
                             description_en   TEXT,
                             duration_days    INTEGER NOT NULL,
                             difficulty       VARCHAR(50) NOT NULL,
                             price            DECIMAL(10,2),
                             currency         VARCHAR(10) DEFAULT 'EUR',
                             price_on_request BOOLEAN DEFAULT FALSE,
                             start_date       DATE,
                             end_date         DATE,
                             spots_total      INTEGER NOT NULL DEFAULT 8,
                             spots_remaining  INTEGER NOT NULL DEFAULT 8,
                             included_fr      TEXT,
                             included_en      TEXT,
                             not_included_fr  TEXT,
                             not_included_en  TEXT,
                             cover_image_url  VARCHAR(500),
                             whatsapp_message TEXT,
                             is_published     BOOLEAN DEFAULT FALSE,
                             created_at       TIMESTAMP NOT NULL DEFAULT NOW(),
                             updated_at       TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Itinerary days
CREATE TABLE itinerary_days (
                                id             UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                expedition_id  UUID NOT NULL REFERENCES expeditions(id) ON DELETE CASCADE,
                                day_number     INTEGER NOT NULL,
                                title_fr       VARCHAR(255),
                                title_en       VARCHAR(255),
                                description_fr TEXT,
                                description_en TEXT
);

-- Expedition images
CREATE TABLE expedition_images (
                                   id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                   expedition_id UUID NOT NULL REFERENCES expeditions(id) ON DELETE CASCADE,
                                   image_url     VARCHAR(500) NOT NULL,
                                   public_id     VARCHAR(255),
                                   caption_fr    VARCHAR(255),
                                   caption_en    VARCHAR(255),
                                   sort_order    INTEGER DEFAULT 0
);

-- Reservations
CREATE TABLE reservations (
                              id               UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                              expedition_id    UUID REFERENCES expeditions(id) ON DELETE SET NULL,
                              full_name        VARCHAR(255) NOT NULL,
                              email            VARCHAR(255) NOT NULL,
                              phone            VARCHAR(50),
                              nationality      VARCHAR(100),
                              participants     INTEGER NOT NULL DEFAULT 1,
                              message          TEXT,
                              status           VARCHAR(50) NOT NULL DEFAULT 'PENDING',
                              whatsapp_sent    BOOLEAN DEFAULT FALSE,
                              admin_notes      TEXT,
                              created_at       TIMESTAMP NOT NULL DEFAULT NOW(),
                              updated_at       TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Blog posts
CREATE TABLE blog_posts (
                            id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                            slug         VARCHAR(255) NOT NULL UNIQUE,
                            title_fr     VARCHAR(255) NOT NULL,
                            title_en     VARCHAR(255),
                            excerpt_fr   TEXT,
                            excerpt_en   TEXT,
                            content_fr   TEXT,
                            content_en   TEXT,
                            cover_image  VARCHAR(500),
                            public_id    VARCHAR(255),
                            author       VARCHAR(255) DEFAULT 'Wonder Mum of 4',
                            is_published BOOLEAN DEFAULT FALSE,
                            published_at TIMESTAMP,
                            created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
                            updated_at   TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Testimonials
CREATE TABLE testimonials (
                              id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                              author_name   VARCHAR(255) NOT NULL,
                              country       VARCHAR(100),
                              expedition_id UUID REFERENCES expeditions(id) ON DELETE SET NULL,
                              rating        INTEGER NOT NULL DEFAULT 5 CHECK (rating BETWEEN 1 AND 5),
                              content_fr    TEXT,
                              content_en    TEXT,
                              photo_url     VARCHAR(500),
                              is_approved   BOOLEAN DEFAULT FALSE,
                              created_at    TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Gallery
CREATE TABLE gallery_items (
                               id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                               image_url   VARCHAR(500) NOT NULL,
                               public_id   VARCHAR(255),
                               category    VARCHAR(100),
                               caption_fr  VARCHAR(255),
                               caption_en  VARCHAR(255),
                               sort_order  INTEGER DEFAULT 0,
                               created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Contact requests
CREATE TABLE contact_requests (
                                  id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                  full_name     VARCHAR(255) NOT NULL,
                                  email         VARCHAR(255) NOT NULL,
                                  phone         VARCHAR(50),
                                  expedition_id UUID REFERENCES expeditions(id) ON DELETE SET NULL,
                                  subject       VARCHAR(255),
                                  message       TEXT NOT NULL,
                                  is_read       BOOLEAN DEFAULT FALSE,
                                  created_at    TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Indexes
CREATE INDEX idx_expeditions_slug       ON expeditions(slug);
CREATE INDEX idx_expeditions_published  ON expeditions(is_published);
CREATE INDEX idx_expeditions_date       ON expeditions(start_date);
CREATE INDEX idx_blog_slug              ON blog_posts(slug);
CREATE INDEX idx_blog_published         ON blog_posts(is_published);
CREATE INDEX idx_testimonials_approved  ON testimonials(is_approved);
CREATE INDEX idx_gallery_category       ON gallery_items(category);
CREATE INDEX idx_reservations_status    ON reservations(status);
CREATE INDEX idx_reservations_expid     ON reservations(expedition_id);
CREATE INDEX idx_contact_is_read        ON contact_requests(is_read);
