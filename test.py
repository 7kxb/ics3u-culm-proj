import sys
import pygame
from pygame.locals import *
pygame.init()

fps = 60
fpsClock = pygame.time.Clock()
width, height = 160, 90
screen = pygame.display.set_mode((width, height))

class paddle1():
    x = 0
    y = height//2-10
    w = 10
    h = 20
    def handle():
        keysPressed = pygame.key.get_pressed()
        if keysPressed[pygame.K_w]:
            paddle1.y -= 2
            if paddle1.y < 0:
                paddle1.y = 0
        if keysPressed[pygame.K_s]:
            paddle1.y += 2
            if paddle1.y > height-paddle1.h:
                paddle1.y = height-paddle1.h
        r = pygame.Rect(paddle1.x,paddle1.y,paddle1.w,paddle1.h)
        pygame.draw.rect(screen,"#FFFFFF",r)

class paddle2():
    x = width-10
    y = height//2-10
    w = 10
    h = 20
    def handle():
        keysPressed = pygame.key.get_pressed()
        if keysPressed[pygame.K_UP]:
            paddle2.y -= 2
            if paddle2.y < 0:
                paddle2.y = 0
        if keysPressed[pygame.K_DOWN]:
            paddle2.y += 2
            if paddle2.y > height-paddle2.h:
                paddle2.y = height-paddle2.h
        r = pygame.Rect(paddle2.x,paddle2.y,paddle2.w,paddle2.h)
        pygame.draw.rect(screen,"#FFFFFF",r)

class ball():
    x = width//2
    y = height//2
    w = 10
    h = 10
    dx = 2
    dy = 2
    def handle():
        ball.x += ball.dx
        ball.y += ball.dy
        if ball.x < 0 or ball.x+ball.w > width:
            ball.dx *= -1
        if ball.y < 0 or ball.y+ball.h > height:
            ball.dy *= -1
        r = pygame.Rect(ball.x,ball.y,ball.w,ball.h)
        pygame.draw.rect(screen,"#FFFFFF",r)

while True:
  screen.fill((0, 0, 0))
  for event in pygame.event.get():
    if event.type == QUIT:
      pygame.quit()
      sys.exit()
  paddle1.handle()
  paddle2.handle()
  ball.handle()
  pygame.display.flip()
  fpsClock.tick(fps)