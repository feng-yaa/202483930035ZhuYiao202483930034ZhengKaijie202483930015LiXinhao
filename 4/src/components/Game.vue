<script>
import { G } from "../assets/map.js";
import { reactive, ref } from 'vue';

export default {
    data() {
        return {
            hp: 105,
            level: 0,
            x: 0,
            y: 0,
            key: 0,
            game: {},
        };
    },
    methods: {
        loadGame(aim) {
            if (0 <= aim && aim < G.contents.length) {
                this.level = aim;
                let T = G.contents[aim];

                T.m = T.m.split('\n');
                let m = [];
                let IMG = [];
                for (let item of T.m) {
                    item = item.trim();
                    let res = [];
                    let imgs = [];
                    item = item.split(' ');
                    for (let block of item) {
                        if (block) {
                            let x = parseInt(block);
                            res.push(x);
                            let g;
                            if (x == 0) { g = "wall"; }
                            if (x == 1) { g = "path"; }
                            if (x == 2) { g = "key"; }
                            if (x == 3) { g = "door"; }
                            if (x == 4) { g = "stair"; }
                            if (x == 5) { g = "exit"; }
                            if (x == 6) { g = "hero"; }
                            if (x < 0) { g = "enemy"; }
                            if (x > 10) { g = "potion"; }
                            imgs.push(g);
                        }
                    }
                    if (res.length)
                        m.push(res), IMG.push(imgs);
                }

                T.m = m;
                T.IMG = IMG;
                this.game = reactive(T);
                this.game.h = G.h;
                this.game.r = G.r;

                for (const i in T.m) {
                    for (let j in T.m[i]) {
                        if (T.m[i][j] == 6)
                            this.x = parseInt(i), this.y = parseInt(j);
                    }
                }
            }
            else {
                alert("bad");
                return;
            }
        },

        myMoveTo(x, y) {
            const X = this.game.h, Y = this.game.r;
            console.log(this.game, x, y);
            if (x < 0 || x >= X || y < 0 || y >= Y)
                return;

            let res = this.game.m[x][y];
            if (res == 0) { return; }
            if (res == 1) { }
            if (res == 2) { this.key++; this.game.m[x][y] = 1; }
            if (res == 3) {
                if (this.key <= 0) return;
                this.key--;
                this.game.m[x][y] = 1;
            }
            if (res == 4) { this.loadGame(this.level + 1); return; }
            if (res == 5) {
                alert("WIN!");
                this.game.m[x][y] = 1;
            }
            if (res == 6) { } // pass
            if (res < 0) {
                let atk = -res;
                if (this.hp <= atk) return;
                this.hp -= atk;
                this.game.m[x][y] = 1;
            }
            if (res > 10) {
                this.hp += res;
                this.game.m[x][y] = 1;
            }

            this.game.IMG[this.x][this.y] = 'path';
            this.x = x, this.y = y;
            this.game.IMG[x][y] = 'hero';
        },

        handleKeydown(event) {
            const key = event.key.toLowerCase();
            switch (key) {
                case 'w':
                    this.myMoveTo(this.x - 1, this.y);
                    break;
                case 'a':
                    this.myMoveTo(this.x, this.y - 1);
                    break;
                case 's':
                    this.myMoveTo(this.x + 1, this.y);
                    break;
                case 'd':
                    this.myMoveTo(this.x, this.y + 1);
                    break;
                default:
                    break;
            }
        }
    },

    mounted() {
        window.addEventListener('keydown', this.handleKeydown);
        this.loadGame(0);
    },
    beforeDestroy() {
        window.removeEventListener('keydown', this.handleKeydown);
    },
};
</script>


<template>
    <div class="box">
        <div class="info">
            <h1>生命值 {{ hp }}, 钥匙 {{ key }}</h1>
            <p>按键操作:</p>
            <ul>
                <li>W：上移</li>
                <li>A：左移</li>
                <li>S：下移</li>
                <li>D：右移</li>
            </ul>
            <p>当前位置坐标：({{ x }}, {{ y }})</p>
        </div>

        <div class="mapBox">
            <div class="mapPadding">
                <div v-for="(row, i) in this.game.m" class="map-row">
                    <div v-for="(col, j) in row" :class="this.game.IMG[i][j]"></div>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
* {
    --var-h: 8vh;
}

.info {
    width: fit-content;
}

.box {
    display: flex;
    flex-wrap: nowrap;
    flex-direction: row;
    justify-content: space-between;
}

.mapBox {
    width: 60vw;
}

.mapPadding {
    width: 50vh;
    height: 50vh;
    margin: 0 auto;
}

.map-row {
    height: var(--var-h);
    padding: 0;
    margin: 0;
}

.map-row div {
    display: inline-block;
    height: var(--var-h);
    width: var(--var-h);
    background-size: contain;
}

.wall {
    background-image: url('@/assets/Wall.jpg');
}

.path {
    background-image: url('@/assets/Floor.jpg');
}

.key {
    background-image: url('@/assets/Key.jpg');
}

.door {
    background-image: url('@/assets/Door.jpg');
    background-position-x: 0.3vh;
}

.stair {
    background-image: url('@/assets/Stair.jpg');
}

.exit {
    background-image: url('@/assets/Exit.jpg');
}

.enemy {
    background-image: url('@/assets/Monster.jpg');
}

.potion {
    background-image: url('@/assets/Potion.jpg');
}

.hero {
    background-image: url('@/assets/Hero.jpg');
    background-size: contain;
    background-position-x: -0.1vh;
}
</style>
